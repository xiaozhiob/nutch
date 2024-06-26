/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.urlfilter.ignoreexempt;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.net.URLExemptionFilter;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.urlfilter.regex.RegexURLFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.List;

/**
 * This implementation of {@link org.apache.nutch.net.URLExemptionFilter} 
 * uses regex configuration to check if URL is eligible for exemption from 
 * the <code>db.ignore.external.links</code> configuration property.
 * When this filter is enabled, the external urls will be checked 
 * against configured sequence of regex rules.
 *<p>
 * The exemption rule file defaults to 
 * <code>db-ignore-external-exemptions.txt</code> in the classpath but 
 * can be overridden using the configuration property 
 * <code>db.ignore.external.exemptions.file</code>.
 *</p>
 *
 * The exemption rules are specified in plain text file where each line 
 * is a rule.
 * The format is same same as <code>regex-urlfilter.txt</code>.
 * Each non-comment, non-blank line contains a regular expression
 * prefixed by <b>+</b> or <b>-</b>. The first matching pattern in the file
 * determines whether a URL is exempted or ignored. If no pattern
 * matches, the URL is ignored.
 *
 * @since Feb 10, 2016
 * @version 1
 * @see org.apache.nutch.net.URLExemptionFilter
 * @see org.apache.nutch.urlfilter.regex.RegexURLFilter
 */
public class ExemptionUrlFilter extends RegexURLFilter
    implements URLExemptionFilter {

  public static final String DB_IGNORE_EXTERNAL_EXEMPTIONS_FILE
      = "db.ignore.external.exemptions.file";

  private List<Pattern> exemptions;

  public List<Pattern> getExemptions() {
    return exemptions;
  }

  @Override
  public boolean filter(String fromUrl, String toUrl) {
    //this implementation does not consider fromUrl param.
    //the regex rules are applied to toUrl.
    return this.filter(toUrl) != null;
  }

  /**
   * Gets reader for regex rules
   */
  @Override
  protected Reader getRulesReader(Configuration conf)
      throws IOException {
    String fileRules = conf.get(DB_IGNORE_EXTERNAL_EXEMPTIONS_FILE);
    return conf.getConfResourceAsReader(fileRules);
  }

  public static void main(String[] args) {

    if (args.length != 1) {
      System.out.println("Error: Invalid Args");
      System.out.println("Usage: " +
          ExemptionUrlFilter.class.getName() + " <url>");
      return;
    }
    String url = args[0];
    ExemptionUrlFilter instance = new ExemptionUrlFilter();
    instance.setConf(NutchConfiguration.create());
    System.out.println(instance.filter(null, url));
  }
}
