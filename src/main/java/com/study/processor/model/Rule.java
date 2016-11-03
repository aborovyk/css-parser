// Rule.java
//
//
// 161023 sd v
//
//


package com.study.processor.model;


import java.util.List;


// BESCHREIBUNG
//
public final class Rule
{

protected String selector;
public String getSelector() { return selector; }
public void setSelector(String selector) { this.selector = selector; }

protected List<Property> properties;
public List<Property> getProperties() { return properties; }
public void setProperties(List<Property> properties) { this.properties = properties; }

protected int specificity;
public int getSpecificity() { return specificity; }
public void setSpecificity(int specificity) { this.specificity = specificity; }
//----------------------------------------------------------------------


public Rule()
{
}

public Rule(String selector, List<Property> properties, Integer specificity)
{
  this.selector = selector;
  this.properties = properties;
  this.specificity = specificity;
}

//----------------------------------------------------------------------








}
