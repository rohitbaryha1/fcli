/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.cli.common.output.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class xPathOutputFilter{

	@SneakyThrows
	public JsonNode filter(JsonNode jsonNode, String expression) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		XmlMapper xmlMapper = new XmlMapper();

		if(! (jsonNode instanceof ObjectNode)){
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode root = objectMapper.createObjectNode();
			root.set("item", jsonNode);

			jsonNode = root;
		}
		String xmlString = xmlMapper.writeValueAsString(jsonNode).replace("ObjectNode", "content");
		InputSource xmlSource = new InputSource(new StringReader(xmlString));

		ObjectNode nodelist = (ObjectNode) xPath.compile(expression).evaluate(xmlSource, XPathConstants.NODESET);

		System.out.println(nodelist);

		Object obj = xPath.compile(expression).evaluate(xmlSource, XPathConstants.NODESET);

		System.out.println(obj);

		return jsonNode ;
	}

}
