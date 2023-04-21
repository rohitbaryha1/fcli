package com.fortify.cli.util.ncd_report.generator.github;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fortify.cli.common.json.JsonHelper;
import com.fortify.cli.util.ncd_report.collector.INcdReportRepositoryBranchCommitCollector;
import com.fortify.cli.util.ncd_report.collector.INcdReportRepositoryProcessor;
import com.fortify.cli.util.ncd_report.collector.NcdReportResultsCollector;
import com.fortify.cli.util.ncd_report.config.NcdReportCombinedRepoSelectorConfig;
import com.fortify.cli.util.ncd_report.config.NcdReportGitHubOrganizationConfig;
import com.fortify.cli.util.ncd_report.config.NcdReportGitHubSourceConfig;
import com.fortify.cli.util.ncd_report.descriptor.NcdReportBranchCommitDescriptor;
import com.fortify.cli.util.ncd_report.generator.AbstractNcdReportUnirestResultsGenerator;
import com.fortify.cli.util.ncd_report.generator.INcdReportBranchCommitGenerator;

import io.micrometer.common.util.StringUtils;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import kong.unirest.UnirestInstance;

/**
 * This class is responsible for loading repository, branch, commit and author
 * data from GitHub.
 * 
 * @author rsenden
 *
 */
public class NcdReportGitHubResultsGenerator extends AbstractNcdReportUnirestResultsGenerator<NcdReportGitHubSourceConfig> {
    /**
     * Constructor to configure this instance with the given 
     * {@link NcdReportGitHubSourceConfig} and
     * {@link NcdReportResultsCollector}.
     */
    public NcdReportGitHubResultsGenerator(NcdReportGitHubSourceConfig sourceConfig, NcdReportResultsCollector resultsCollector) {        
        super(sourceConfig, resultsCollector);
    }

    /**
     * Primary method for running the generation process, taking the
     * {@link UnirestInstance} provided by our superclass. This gets 
     * the organization configurations, and for each organization, calls 
     * the {@link #run(UnirestInstance, NcdReportGitHubOrganizationConfig)}
     * method to load the repositories for that organization.
     */
    @Override
    protected void run(UnirestInstance unirest) {
        Stream.of(sourceConfig().getOrganizations()).forEach(orgConfig->run(unirest, orgConfig));
    }
    
    /**
     * This method loads the repositories for the organization specified in the
     * given {@link NcdReportGitHubOrganizationConfig}, and passes the descriptor
     * for each repository to the {@link INcdReportRepositoryProcessor} provided 
     * by our {@link NcdReportResultsCollector}. The {@link INcdReportRepositoryProcessor}
     * will in turn call our {@link #generateCommitData(INcdReportRepositoryBranchCommitCollector, UnirestInstance, NcdReportGitHubRepositoryDescriptor)}
     * method to generate commit data for every repository that is not excluded from
     * the report.
     */
    private void run(UnirestInstance unirest, NcdReportGitHubOrganizationConfig orgConfig) {
        String orgName = orgConfig.getName();
        try {
            resultsCollector().progressHelper().writeI18nProgress("fcli.util.ncd-report.loading.github-repositories", orgName);
            HttpRequest<?> req = unirest.get("/orgs/{org}/repos?type=all&per_page=100").routeParam("org", orgName);
            GitHubPagingHelper.pagedRequest(req, ArrayNode.class)
                .ifSuccess(r->r.getBody().forEach(repo->
                    resultsCollector().repositoryProcessor().processRepository(new NcdReportCombinedRepoSelectorConfig(sourceConfig(), orgConfig), getRepoDescriptor(repo), commitGenerator(unirest))));
        } catch ( Exception e ) {
            resultsCollector().errorWriter().addReportError(String.format("Error processing organization: %s (%s)", orgName, sourceConfig().getApiUrl()), e);
        }
    }
    
    /**
     * This method generates an {@link INcdReportBranchCommitGenerator} instance
     * using a lambda expression, which calls our {@link #generateCommitData(INcdReportRepositoryBranchCommitCollector, UnirestInstance, NcdReportGitHubRepositoryDescriptor)}
     * method.
     */
    private INcdReportBranchCommitGenerator<NcdReportGitHubRepositoryDescriptor> commitGenerator(UnirestInstance unirest) {
        return (repoDescriptor, branchCommitCollector) -> generateCommitData(branchCommitCollector, unirest, repoDescriptor);
    }
    
    /**
     * This method generates commit data for the given repository by retrieving
     * all branches, and then invoking the {@link #generateCommitDataForBranches(INcdReportRepositoryBranchCommitCollector, UnirestInstance, NcdReportGitHubRepositoryDescriptor, List)}
     * method to generate commit data for each branch. If no commits are found that
     * match the date range, the {@link #generateMostRecentCommitData(INcdReportRepositoryBranchCommitCollector, UnirestInstance, NcdReportGitHubRepositoryDescriptor, List)}
     * method is invoked to find the most recent commit older than the date range.
     */
    private void generateCommitData(INcdReportRepositoryBranchCommitCollector branchCommitCollector, UnirestInstance unirest, NcdReportGitHubRepositoryDescriptor repoDescriptor) {
        var branchDescriptors = getBranchDescriptors(unirest, repoDescriptor);
        boolean commitsFound = generateCommitDataForBranches(branchCommitCollector, unirest, repoDescriptor, branchDescriptors);
        if ( !commitsFound ) {
            generateMostRecentCommitData(branchCommitCollector, unirest, repoDescriptor, branchDescriptors);
        }
    }

    /**
     * This method loads the latest commit for every branch, then passes the overall
     * latest commit (if found) to the {@link #addCommit(INcdReportRepositoryBranchCommitCollector, NcdReportGitHubRepositoryDescriptor, NcdReportGitHubBranchDescriptor, JsonNode)}
     * method.
     */
    private void generateMostRecentCommitData(INcdReportRepositoryBranchCommitCollector branchCommitCollector, UnirestInstance unirest, NcdReportGitHubRepositoryDescriptor repoDescriptor, List<NcdReportGitHubBranchDescriptor> branchDescriptors) {
        NcdReportGitHubCommitDescriptor mostRecentCommitDescriptor = null;
        NcdReportGitHubBranchDescriptor mostRecentBranchDescriptor = null;
        for ( var branchDescriptor : branchDescriptors ) {
            var currentCommitResponse = getCommitsRequest(unirest, repoDescriptor, branchDescriptor, 1)
                .asObject(ArrayNode.class).getBody();
            if ( currentCommitResponse.size()>0 ) {
                var currentCommitDescriptor = JsonHelper.treeToValue(currentCommitResponse.get(0), NcdReportGitHubCommitDescriptor.class);
                if ( mostRecentCommitDescriptor==null || currentCommitDescriptor.getDate().isAfter(mostRecentCommitDescriptor.getDate()) ) {
                    mostRecentCommitDescriptor = currentCommitDescriptor;
                    mostRecentBranchDescriptor = branchDescriptor;
                }
            }
        }
        if ( mostRecentCommitDescriptor!=null ) {
            addCommit(branchCommitCollector, repoDescriptor, mostRecentBranchDescriptor, mostRecentCommitDescriptor.asJsonNode());
        }
    }

    /**
     * This method generates commit data for all commits later than the configured
     * date/time for all branches.
     * @return true if any commits were found, false otherwise  
     */
    private boolean generateCommitDataForBranches(INcdReportRepositoryBranchCommitCollector branchCommitCollector, UnirestInstance unirest, NcdReportGitHubRepositoryDescriptor repoDescriptor, List<NcdReportGitHubBranchDescriptor> branchDescriptors) {
        String since = resultsCollector().reportConfig().getCommitOffsetDateTime()
                .format(DateTimeFormatter.ISO_INSTANT);
        boolean commitsFound = false;
        for ( var branchDescriptor : branchDescriptors ) {
            resultsCollector().progressHelper().writeI18nProgress("fcli.util.ncd-report.loading.branch-commits", repoDescriptor.getFullName(), branchDescriptor.getName());
            HttpRequest<?> req = getCommitsRequest(unirest, repoDescriptor, branchDescriptor, 100)
                    .queryString("since", since);
            
            List<ArrayNode> bodies = GitHubPagingHelper.pagedRequest(req, ArrayNode.class).getBodies();
            for ( ArrayNode body : bodies ) {
                for ( JsonNode commit : body ) {
                    commitsFound = true;
                    addCommit(branchCommitCollector, repoDescriptor, branchDescriptor, commit);
                }
            }
        }
        return commitsFound;
    }
    
    /**
     * Add commit data to the given {@link INcdReportRepositoryBranchCommitCollector}.
     */
    private void addCommit(INcdReportRepositoryBranchCommitCollector branchCommitCollector, NcdReportGitHubRepositoryDescriptor repoDescriptor, NcdReportGitHubBranchDescriptor branchDescriptor, JsonNode commit) {
        var commitDescriptor = JsonHelper.treeToValue(commit, NcdReportGitHubCommitDescriptor.class);
        var authorDescriptor = JsonHelper.treeToValue(commit, NcdReportGitHubAuthorDescriptor.class);
        branchCommitCollector.reportBranchCommit(new NcdReportBranchCommitDescriptor(repoDescriptor, branchDescriptor, commitDescriptor, authorDescriptor));
    }
    
    /**
     * Get the branch descriptors for the repository described by the given
     * repository descriptor.
     */
    private List<NcdReportGitHubBranchDescriptor> getBranchDescriptors(UnirestInstance unirest, NcdReportGitHubRepositoryDescriptor repoDescriptor) {
        List<NcdReportGitHubBranchDescriptor> result = new ArrayList<>(); 
        GitHubPagingHelper.pagedRequest(getBranchesRequest(unirest, repoDescriptor), ArrayNode.class)
            .ifSuccess(r->r.getBody().forEach(b->result.add(JsonHelper.treeToValue(b, NcdReportGitHubBranchDescriptor.class))));
        return result;
    }
    
    /**
     * Get the base request for loading commit data for the repository 
     * and branch described by the given descriptors.
     */
    private GetRequest getCommitsRequest(UnirestInstance unirest, NcdReportGitHubRepositoryDescriptor descriptor, NcdReportGitHubBranchDescriptor branchDescriptor, int perPage) {
        return unirest.get("/repos/{owner}/{repo}/commits")
                .routeParam("owner", descriptor.getOwnerName())
                .routeParam("repo", descriptor.getName())
                .queryString("sha", branchDescriptor.getSha())
                .queryString("per_page", perPage);
    }
    
    /**
     * Get the base request for loading branch data for the repository
     * described by the given repository descriptor.
     */
    private GetRequest getBranchesRequest(UnirestInstance unirest, NcdReportGitHubRepositoryDescriptor descriptor) {
        return unirest.get("/repos/{owner}/{repo}/branches?per_page=100")
                .routeParam("owner", descriptor.getOwnerName())
                .routeParam("repo", descriptor.getName());
    }
    
    /**
     * Convert the given {@link JsonNode} to an 
     * {@link NcdReportGitHubRepositoryDescriptor} instance.
     */
    private NcdReportGitHubRepositoryDescriptor getRepoDescriptor(JsonNode repoNode) {
        return JsonHelper.treeToValue(repoNode, NcdReportGitHubRepositoryDescriptor.class);
    }

    /**
     * Optionally configure an Authorization header to the configuration
     * of the given {@link UnirestInstance}, based on the optional
     * tokenExpression provided in the source configuration. 
     */
    @Override
    protected void configure(UnirestInstance unirest) {
        String tokenExpression = sourceConfig().getTokenExpression();
        if ( StringUtils.isNotBlank(tokenExpression) ) {
            // TODO Doesn't really make sense to use this method with null input object
            //      We should have a corresponding method in SpelHelper that doesn't take
            //      any input
            String token = JsonHelper.evaluateSpelExpression(null, tokenExpression, String.class);
            if ( StringUtils.isBlank(token) ) {
                throw new IllegalStateException("No token found from expression: "+tokenExpression);
            } else {
                unirest.config().addDefaultHeader("Authorization", "Bearer "+token);
            }
        }
    }
    
    /**
     * Return the source type, 'github' in this case.
     */
    @Override
    protected String getType() {
        return "github";
    }
}
