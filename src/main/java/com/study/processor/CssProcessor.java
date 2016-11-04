package com.study.processor;

import com.steadystate.css.parser.LexicalUnitImpl;
import com.steadystate.css.parser.selectors.ConditionalSelectorImpl;
import com.steadystate.css.parser.selectors.DescendantSelectorImpl;
import com.study.processor.model.Property;
import com.study.processor.model.Rule;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SimpleSelector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * aborovyk
 * 03.11.16.
 */
public final class CssProcessor implements DocumentHandler
{


// XXX create on start-event
private Rule currentRule;

private List<Rule> rules = new ArrayList<Rule>();


//----------------------------------------------------------------------
/// XXX adhere to styleguide - i moved things ab bit around ...


public CssProcessor()
{
}


//----------------------------------------------------------------------


// XXX dont work on strings this quickly becomes faulty (e.g. classes need not always be written with "." notation), traverse the sac datastructures
//
private int calculateRuleSpecificity(Selector selector)
{
  int specificity = 0;
  short selectorType = selector.getSelectorType();
  switch (selectorType) {
    //conditional
    case 0: {
      specificity += calculateIdAndClassSelectorsSpecificity((ConditionalSelectorImpl) selector);
      break;
    }
    //element
    case 4: {
      specificity += 1;
      break;
    }
    //descendant
    case 10: {
      specificity += calculateAncestorSelectorSpecificity((DescendantSelectorImpl) selector);
      break;
    }
    default: {
      throw new CSSException("CssProcessor.calculateRuleSpecificity: selector" + selector.toString() +
              " has an unimplemented type: " + selectorType);
    }
  }
  return specificity;
}


//calculate ancestor selector specificity
private int calculateAncestorSelectorSpecificity(DescendantSelectorImpl selector)
{
  int result = 0;
  Selector ancestorSelector = selector.getAncestorSelector();
  result += calculateConditionSelectorSpecificity(ancestorSelector, ancestorSelector.getSelectorType());
  SimpleSelector simpleSelector = selector.getSimpleSelector();
  short selectorType = simpleSelector.getSelectorType();
  if (selectorType == 0) {
    result += calculateIdAndClassSelectorsSpecificity((ConditionalSelectorImpl) simpleSelector);
  }
  if (selectorType == 4) result += 1;
  return result;
}


//calculate id and class selectors specificity
private int calculateIdAndClassSelectorsSpecificity(ConditionalSelectorImpl selector)
{
  int result = 0;
  Condition condition = selector.getCondition();
  //id
  if (condition.getConditionType() == 5) result = 100;
  //class
  if (condition.getConditionType() == 9) result = 10;
  return result;
}


//calculate condition selector specificity
private int calculateConditionSelectorSpecificity(Selector selector, short selectorType)
{
  int result = 0;
  if (selectorType == 0) {
    result += calculateIdAndClassSelectorsSpecificity((ConditionalSelectorImpl) selector);
  }
  if (selectorType == 4) result += 1;
  if (selectorType == 10) {
    result += calculateAncestorSelectorSpecificity((DescendantSelectorImpl) selector);
  }
  return result;
}


//----------------------------------------------------------------------
// sac ifc


@Override
public void startDocument(InputSource inputSource) throws CSSException
{

}


@Override
public void endDocument(InputSource inputSource) throws CSSException
{

}

@Override
public void comment(String s) throws CSSException
{

}


@Override
public void ignorableAtRule(String s) throws CSSException
{

}


@Override
public void namespaceDeclaration(String s, String s2) throws CSSException
{

}

@Override
public void importStyle(String s, SACMediaList sacMediaList, String s2) throws CSSException
{

}


@Override
public void startMedia(SACMediaList sacMediaList) throws CSSException
{

}

@Override
public void endMedia(SACMediaList sacMediaList) throws CSSException
{

}


@Override
public void startPage(String s, String s2) throws CSSException
{

}


@Override
public void endPage(String s, String s2) throws CSSException
{

}


@Override
public void startFontFace() throws CSSException
{

}

@Override
public void endFontFace() throws CSSException
{

}


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
      rule.setProperties(new ArrayList<Property>(properties));
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


//----------------------------------------------------------------------
// static


// XXX look into spec how to obtain file name
//
public static void main(String[] args)
{
  try {
    CssProcessor cssProcessor = new CssProcessor();
    URL resource = cssProcessor.getClass().getClassLoader().getResource(args[0]);
    if (resource != null) {
      FileInputStream stream = new FileInputStream( resource.getFile());
      InputSource source = new InputSource(new InputStreamReader(stream));
      List<Rule> result = CssParser.parseCss(source);
      for (Rule rule : result) {
        System.out.println(rule);
      }
    } else {
      System.out.println("file " + args[0] + " not found");
    }
  } catch (FileNotFoundException e) {
  }
}






}