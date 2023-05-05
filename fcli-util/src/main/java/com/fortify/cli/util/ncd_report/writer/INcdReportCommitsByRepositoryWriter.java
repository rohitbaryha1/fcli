package com.fortify.cli.util.ncd_report.writer;

import com.fortify.cli.util.ncd_report.descriptor.INcdReportCommitDescriptor;
import com.fortify.cli.util.ncd_report.descriptor.INcdReportRepositoryDescriptor;
import com.fortify.cli.util.ncd_report.descriptor.NcdReportProcessedAuthorDescriptor;

public interface INcdReportCommitsByRepositoryWriter {
    void writeRepositoryCommit(INcdReportRepositoryDescriptor repositoryDescriptor, INcdReportCommitDescriptor commitDescriptor, NcdReportProcessedAuthorDescriptor contributorDescriptor);
}