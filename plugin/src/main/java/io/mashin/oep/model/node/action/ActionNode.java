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

package io.mashin.oep.model.node.action;

import io.mashin.oep.hpdl.XMLReadUtils;
import io.mashin.oep.hpdl.XMLWriteUtils;
import io.mashin.oep.model.SchemaVersion;
import io.mashin.oep.model.Workflow;
import io.mashin.oep.model.connection.WorkflowConnection;
import io.mashin.oep.model.connection.WorkflowConnectionDummyEndPoint;
import io.mashin.oep.model.connection.WorkflowConnectionEndPoint;
import io.mashin.oep.model.node.Node;
import io.mashin.oep.model.property.SLAPropertyElement;
import io.mashin.oep.model.property.TextPropertyElement;
import io.mashin.oep.model.property.filter.SchemaVersionRangeFilter;
import io.mashin.oep.model.terminal.FanInTerminal;
import io.mashin.oep.model.terminal.SingleOutputTerminal;

import java.util.Arrays;
import java.util.List;

import org.dom4j.Element;

public abstract class ActionNode extends Node {
  
  public static final String PROP_NODE_CRED          = "prop.node.action.cred";
  public static final String PROP_NODE_RETRYMAX      = "prop.node.action.retry-max";
  public static final String PROP_NODE_RETRYINTERVAL = "prop.node.action.retry-interval";
  public static final String PROP_NODE_SLA           = "prop.node.action.sla";
  
  protected FanInTerminal fanInTerminal;
  protected SingleOutputTerminal okSingleOutputTerminal;
  protected SingleOutputTerminal errSingleOutputTerminal;
  
  protected TextPropertyElement cred;
  protected TextPropertyElement retryMax;
  protected TextPropertyElement retryInterval;
  protected SLAPropertyElement  sla;
  
  public ActionNode(Workflow workflow) {
    this(workflow, null);
  }

  public ActionNode(Workflow workflow, org.dom4j.Node hpdlNode) {
    super(workflow, hpdlNode);
    
    fanInTerminal           = new FanInTerminal(TERMINAL_FANIN, this);
    okSingleOutputTerminal  = new SingleOutputTerminal(TERMINAL_OK, this);
    errSingleOutputTerminal = new SingleOutputTerminal(TERMINAL_ERROR, this);
    terminals.add(fanInTerminal);
    terminals.add(okSingleOutputTerminal);
    terminals.add(errSingleOutputTerminal);
    
    cred = new TextPropertyElement(PROP_NODE_CRED, "Cred",
        new SchemaVersionRangeFilter(SchemaVersion.V_0_2_5, SchemaVersion.V_ANY, workflow));
    addPropertyElement(cred);
    
    retryMax = new TextPropertyElement(PROP_NODE_RETRYMAX, "Retry Max",
        new SchemaVersionRangeFilter(SchemaVersion.V_0_3, SchemaVersion.V_ANY, workflow));
    addPropertyElement(retryMax);
    
    retryInterval = new TextPropertyElement(PROP_NODE_RETRYINTERVAL, "Retry Interval",
        new SchemaVersionRangeFilter(SchemaVersion.V_0_3, SchemaVersion.V_ANY, workflow));
    addPropertyElement(retryInterval);
    
    sla = new SLAPropertyElement(PROP_NODE_SLA, "SLA", workflow,
        new SchemaVersionRangeFilter(SchemaVersion.V_0_2, SchemaVersion.V_ANY, workflow)
        .and(pe -> SchemaVersion.V_0_1.equals(workflow.getSLAVersion())),
        new SchemaVersionRangeFilter(SchemaVersion.V_0_5, SchemaVersion.V_ANY, workflow)
        .and(pe -> SchemaVersion.V_0_2.equals(workflow.getSLAVersion())));
    addPropertyElement(sla);
  }
  
  @Override
  public void initDefaults() {
    super.initDefaults();
  }
  
  @Override
  public void write(Element parent) {
    super.write(parent);
    
    Element element = (Element) hpdlModel.get();
    
    element.setName("action");
    XMLWriteUtils.writeTextPropertyAsAttribute(cred, element, "cred");
    XMLWriteUtils.writeTextPropertyAsAttribute(retryMax, element, "retry-max");
    XMLWriteUtils.writeTextPropertyAsAttribute(retryInterval, element, "retry-interval");
  }
  
  @Override
  protected void writeConnections(Element nodeElement) {
    super.writeConnections(nodeElement);
    
    XMLWriteUtils.writeConnectionsAsElementWithAttribute(
        okSingleOutputTerminal.getConnections(), nodeElement, "ok", "to");
    XMLWriteUtils.writeConnectionsAsElementWithAttribute(
        errSingleOutputTerminal.getConnections(), nodeElement, "error", "to");
    
    XMLWriteUtils.writeSLAProperty(workflow, sla, nodeElement);
  }
  
  @Override
  public void read(org.dom4j.Node hpdlNode) {
    super.read(hpdlNode);
    
    XMLReadUtils.initTextPropertyFrom(cred, hpdlNode, "@cred");
    XMLReadUtils.initTextPropertyFrom(retryMax, hpdlNode, "@retry-max");
    XMLReadUtils.initTextPropertyFrom(retryInterval, hpdlNode, "@retry-interval");
    XMLReadUtils.initSLAPropertyFrom(sla, hpdlNode, "./sla:info");
    
    // read connections
    
    String okConn = XMLReadUtils.valueOf("./ok/@to", hpdlNode);
    if (!okConn.isEmpty()) {
      WorkflowConnection conn = new WorkflowConnection(
          new WorkflowConnectionEndPoint(this, okSingleOutputTerminal),
          new WorkflowConnectionDummyEndPoint(okConn, TERMINAL_FANIN));
      sourceConnections.add(conn);
    }
    
    String errConn = XMLReadUtils.valueOf("./error/@to", hpdlNode);
    if (!errConn.isEmpty()) {
      WorkflowConnection conn = new WorkflowConnection(
          new WorkflowConnectionEndPoint(this, errSingleOutputTerminal),
          new WorkflowConnectionDummyEndPoint(errConn, TERMINAL_FANIN));
      sourceConnections.add(conn);
    }
  }
      
  
  public void setCred(String cred) {
    setPropertyValue(PROP_NODE_CRED, cred);
  }
  
  public String getCred() {
    return cred.getStringValue();
  }
  
  public void setRetryMax(String retryMax) {
    setPropertyValue(PROP_NODE_RETRYMAX, retryMax);
  }
  
  public String getRetryMax() {
    return retryMax.getStringValue();
  }
  
  public void setRetryInterval(String retryInterval) {
    setPropertyValue(PROP_NODE_RETRYINTERVAL, retryInterval);
  }
  
  public String getRetryInterval() {
    return retryInterval.getStringValue();
  }
  
  @Override
  public List<SchemaVersion> getPossibleSchemaVersions() {
    if (this.workflow == null) {
      return Arrays.asList(SchemaVersion.V_ANY);
    }
    return this.workflow.getPossibleSchemaVersions();
  }

  @Override
  public SchemaVersion getDefaultSchemaVersion() {
    if (this.workflow == null) {
      return SchemaVersion.V_ANY;
    }
    return this.workflow.getSchemaVersion();
  }

  @Override
  public SchemaVersion getLatestSchemaVersion() {
    if (this.workflow == null) {
      return SchemaVersion.V_ANY;
    }
    return this.workflow.getLatestSchemaVersion();
  }
  
  @Override
  protected boolean isSchemaVersionEditable() { return false; }
  
}
