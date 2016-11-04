// Rule.java
//
//
// 161023 sd v
//
//


package com.study.processor.model;


import org.w3c.css.sac.Selector;

import java.util.List;


// BESCHREIBUNG
//
public final class Rule
{


protected Selector selector;
public Selector getSelector() { return selector; }
public void setSelector(Selector selector) { this.selector = selector; }

protected List<Property> properties;
public List<Property> getProperties() { return properties; }
public void setProperties(List<Property> properties) { this.properties = properties; }

protected int specificity;
public int getSpecificity() { return specificity; }
public void setSpecificity(int specificity) { this.specificity = specificity; }


//----------------------------------------------------------------------


public Rule()
{ }


//----------------------------------------------------------------------


@Override
public String toString()
{
  return "Rule{" +
          "selector='" + selector + '\'' +
          ", properties=" + properties +
          ", specificity=" + Integer.toString(specificity, 16) +
          '}';
}


}
