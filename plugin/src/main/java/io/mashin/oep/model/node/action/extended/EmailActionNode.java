/**
 * Copyright (c) 2015 Mashin (http://mashin.io). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mashin.oep.model.node.action.extended;

import io.mashin.oep.hpdl.XMLReadUtils;
import io.mashin.oep.hpdl.XMLWriteUtils;
import io.mashin.oep.model.SchemaVersion;
import io.mashin.oep.model.Workflow;
import io.mashin.oep.model.property.TextPropertyElement;
import io.mashin.oep.model.property.filter.SchemaVersionRangeFilter;

import java.util.Arrays;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public class EmailActionNode extends ExtendedActionNode {

  private static final List<SchemaVersion> EMAIL_POSSIBLE_SCHEMA_VERSIONS = Arrays
      .asList(SchemaVersion.V_0_1, SchemaVersion.V_0_2);
  private static final SchemaVersion EMAIL_DEFAULT_SCHEMA_VERSION = SchemaVersion.V_0_2;
  private static final SchemaVersion EMAIL_LATEST_SCHEMA_VERSION = SchemaVersion.V_0_2;

  public static final String PROP_TO = "prop.node.email.to";
  public static final String PROP_CC = "prop.node.email.cc";
  public static final String PROP_SUBJECT = "prop.node.email.subject";
  public static final String PROP_BODY = "prop.node.email.body";
  public static final String PROP_CONTENT_TYPE = "prop.node.email.content_type";
  public static final String PROP_ATTACHMENT = "prop.node.email.attachment";
  
  protected TextPropertyElement to;//to
  protected TextPropertyElement cc;//cc
  protected TextPropertyElement subject;//subject
  protected TextPropertyElement body;//body
  protected TextPropertyElement contentType;//content_type
  protected TextPropertyElement attachment;//attachment
  
  public EmailActionNode(Workflow workflow) {
    this(workflow, null);
  }
  
  public EmailActionNode(Workflow workflow, Node hpdlNode) {
    super(workflow, hpdlNode);
    
    to = new TextPropertyElement(PROP_TO, "To");
    addPropertyElement(to);
    
    cc = new TextPropertyElement(PROP_CC, "CC");
    addPropertyElement(cc);
    
    subject = new TextPropertyElement(PROP_SUBJECT, "Subject");
    addPropertyElement(subject);
    
    body = new TextPropertyElement(PROP_BODY, "Body");
    addPropertyElement(body);
    
    contentType = new TextPropertyElement(PROP_CONTENT_TYPE, "Content Type",
        new SchemaVersionRangeFilter(SchemaVersion.V_0_2, SchemaVersion.V_ANY, this));
    addPropertyElement(contentType);
    
    attachment = new TextPropertyElement(PROP_ATTACHMENT, "Attachment",
        new SchemaVersionRangeFilter(SchemaVersion.V_0_2, SchemaVersion.V_ANY, this));
    addPropertyElement(attachment);
  }

  @Override
  public void initDefaults() {
    super.initDefaults();
    setName(workflow.nextId("email"));
  }
  
  @Override
  public void write(Element paretNode) {
    super.write(paretNode);
    
    Element element = (Element) hpdlModel.get();
    Element email = element.addElement("email");
    
    XMLWriteUtils.writeSchemaVersion(getSchemaVersion(), email, getNodeType());
    XMLWriteUtils.writeTextPropertyAsElement(to, email, "to");
    XMLWriteUtils.writeTextPropertyAsElement(cc, email, "cc");
    XMLWriteUtils.writeTextPropertyAsElement(subject, email, "subject");
    XMLWriteUtils.writeTextPropertyAsElement(body, email, "body");
    XMLWriteUtils.writeTextPropertyAsElement(contentType, email, "content_type");
    XMLWriteUtils.writeTextPropertyAsElement(attachment, email, "attachment");
    
    writeConnections(element);
  }
  
  @Override
  public void read(Node hpdlNode) {
    super.read(hpdlNode);
    
    XMLReadUtils.initTextPropertyFrom(to, hpdlNode, "./email/to");
    XMLReadUtils.initTextPropertyFrom(cc, hpdlNode, "./email/cc");
    XMLReadUtils.initTextPropertyFrom(subject, hpdlNode, "./email/subject");
    XMLReadUtils.initTextPropertyFrom(body, hpdlNode, "./email/body");
    XMLReadUtils.initTextPropertyFrom(contentType, hpdlNode, "./email/content_type");
    XMLReadUtils.initTextPropertyFrom(attachment, hpdlNode, "./email/attachment");
  }
  
  @Override
  public String getNodeType() {
    return TYPE_EMAIL;
  }
  
  @Override
  public List<SchemaVersion> getPossibleSchemaVersions() {
    return EMAIL_POSSIBLE_SCHEMA_VERSIONS;
  }

  @Override
  public SchemaVersion getDefaultSchemaVersion() {
    return EMAIL_DEFAULT_SCHEMA_VERSION;
  }

  @Override
  public SchemaVersion getLatestSchemaVersion() {
    return EMAIL_LATEST_SCHEMA_VERSION;
  }

}
