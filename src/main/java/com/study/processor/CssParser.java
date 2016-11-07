package com.study.processor;

import com.steadystate.css.parser.LexicalUnitImpl;
import com.study.processor.model.Property;
import com.study.processor.model.Rule;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.helpers.ParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * aborovyk
 * 04.11.16.
 */
public class CssParser extends NullDocumentHandler
{


private final static int ID_SPECIFICITY_VALUE = 100;
private final static int CLASS_SPECIFICITY_VALUE = 10;
private final static int ELEMENT_SPECIFICITY_VALUE = 1;

// XXX create on start-event
private Rule currentRule;

private List<Rule> rules = new ArrayList<>();


//----------------------------------------------------------------------


// ZZZ more clearly use startSelector to create the datat for the "currentRule" here
// ZZZ more precisely: we dont need a current "rule" but only the list of properties
@Override
public void startSelector(SelectorList selectorList) throws CSSException
{
  if (currentRule == null) {
    currentRule = new Rule();
    currentRule.setProperties(new ArrayList<Property>());
  }
}

@Override
public void endSelector(SelectorList selectorList) throws CSSException
{
  // ZZZ possibly drop empty rules with no properties
  for (int i = 0; i < selectorList.getLength(); i++) {
    List<Property> properties = currentRule.getProperties();
    if (!properties.isEmpty()) {
      Rule rule = new Rule();
      Selector selector = selectorList.item(i);
      rule.setSelector(selector);
      rule.setProperties(new ArrayList<>(properties));
      // "& 0xff certainly" is not enough
      // ? we set here 8-bit representation of an int value, is it correct?
      rule.setSpecificity(calculateRuleSpecificity(selector) & 0xff);
      // XXX create rule besed on current property list; must copy all properties here when we want to use specificity within each property (which is a good idea)
      rules.add(rule);
    }
  }
  currentRule.getProperties().clear();
}


@Override
public void property(String name, LexicalUnit value, boolean b) throws CSSException
{
  currentRule.getProperties().add(new Property(name, ((LexicalUnitImpl) value).getCssText()));
}


// XXX dont work on strings this quickly becomes faulty (e.g. classes need not always be written with "." notation), traverse the sac datastructures
//
private int calculateRuleSpecificity(Selector selector)
{
  int specificity = 0;
  short selectorType = selector.getSelectorType();
  switch (selectorType) {
    //conditional
    case Selector.SAC_CONDITIONAL_SELECTOR: {
      specificity += calculateSpecificity((ConditionalSelector) selector);
      break;
    }
    //element
    case Selector.SAC_ELEMENT_NODE_SELECTOR: {
      specificity += ELEMENT_SPECIFICITY_VALUE;
      break;
    }
    //descendant
    case Selector.SAC_DESCENDANT_SELECTOR: {
      specificity += calculateSpecificity((DescendantSelector) selector);
      break;
    }
    default: {
      System.out.println("CssProcessor.calculateRuleSpecificity: selector" + selector.toString() +
              " has an unimplemented type: " + selectorType);
      throw new CSSException("CssProcessor.calculateRuleSpecificity: selector" + selector.toString() +
              " has an unimplemented type: " + selectorType);
    }
  }
  return specificity;
}


// ZZZ probably a method named "calculateSpecificity" could be overloaded so we dont need different and long method names

// calculate ancestor selector specificity
//
private int calculateSpecificity(DescendantSelector selector)
{
  int result = 0;
  Selector ancestorSelector = selector.getAncestorSelector();
  result += calculateSpecificity(ancestorSelector, ancestorSelector.getSelectorType());
  SimpleSelector simpleSelector = selector.getSimpleSelector();
  short selectorType = simpleSelector.getSelectorType();
  if (selectorType == Selector.SAC_CONDITIONAL_SELECTOR) {
    // XXX never cast to (or use or even import) impl classes, always use the sac api interfaces
    result += calculateSpecificity((ConditionalSelector) simpleSelector);
  }
  // XXX never use plain numbers, always use the constant names from the sac api
  if (selectorType == Selector.SAC_ELEMENT_NODE_SELECTOR) result += ELEMENT_SPECIFICITY_VALUE;
  return result;
}


// calculate id and class selectors
//
private int calculateSpecificity(ConditionalSelector selector)
{
  int result = 0;
  Condition condition = selector.getCondition();
  //id
  // XXX 10/100 is wrong, use 3x8-bit encoding (and static named constants if you need them)
  // XXX never use 5 or 9, always use the constant names from the sac api
  if (condition.getConditionType() == Condition.SAC_ID_CONDITION) result = ID_SPECIFICITY_VALUE;
  //class
  if (condition.getConditionType() == Condition.SAC_CLASS_CONDITION) result = CLASS_SPECIFICITY_VALUE;
  return result;
}


// ZZZ styleguide: space after "//" and empty "//" line between comment and method signature
// calculate condition selector specificity
//
private int calculateSpecificity(Selector selector, short selectorType)
{
  int result = 0;
  if (selectorType == Selector.SAC_CONDITIONAL_SELECTOR) {
    result += calculateSpecificity((ConditionalSelector) selector);
  }
  if (selectorType == Selector.SAC_ELEMENT_NODE_SELECTOR) result += ELEMENT_SPECIFICITY_VALUE;
  if (selectorType == Selector.SAC_DESCENDANT_SELECTOR) {
    result += calculateSpecificity((DescendantSelector) selector);
  }
  return result;
}

// XXX i would brefer the parser in an extra class "CssParser" becouse it is completely different functionality than the actual xml "processor"
// XXX this includes all DocumentHandler methods; actually more or less everything that is currently in the CssProcessor
// XXX and use a baseclass "NullDocumentHandler implements DocumentHandler" with all empty methods to keep the CssParser clean of empty methods
public List<Rule> parseCss(InputSource source)
{
  // ZZZ can the standardized org.w3c.css.sac.helpers.ParserFactory be used ?
  System.setProperty("org.w3c.css.sac.parser", "com.steadystate.css.parser.SACParserCSS3");
  ParserFactory parserFactory = new ParserFactory();
  try {
    Parser parser = parserFactory.makeParser();
    parser.setDocumentHandler(this);
    parser.parseStyleSheet(source);
  } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IOException e) {
    System.out.println("CssProcessor.parseCss: " + e.getMessage());
  }
  return rules;
}


}
