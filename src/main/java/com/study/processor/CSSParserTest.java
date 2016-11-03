package com.study.processor;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS2;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * aborovyk
 * 03.11.16.
 */
public class CSSParserTest
{

public static void main(String[] args) {

  CSSParserTest cssParserTest = new CSSParserTest();
  cssParserTest.parse("/android.css");
}


public boolean parse(String cssfile)
{

  FileOutputStream out;
  PrintStream ps = null;
  boolean result = false;

  try
  {
    FileInputStream stream = new FileInputStream(getClass().getResource(cssfile).getFile());
    out = new FileOutputStream("log.txt");

    ps = new PrintStream( out );

    InputSource source = new InputSource(new InputStreamReader(stream));
    CSSOMParser parser = new CSSOMParser();
    CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

    CSSRuleList rules = stylesheet.getCssRules();

    ps.println("Number of rules: " + rules.getLength());


    for (int i = 0; i < rules.getLength(); i++)
    {
      CSSRule rule = rules.item(i);
      if (rule instanceof CSSStyleRule)
      {
        CSSStyleRule styleRule=(CSSStyleRule)rule;
        ps.println("selector " + (i + 1) + ": " + styleRule.getSelectorText());
        CSSStyleDeclaration styleDeclaration = styleRule.getStyle();


        for (int j = 0; j < styleDeclaration.getLength(); j++)
        {
          ps.println("---------------------------------------");
          String property = styleDeclaration.item(j);
          ps.println("property: " + property);
          ps.println("value: " + styleDeclaration.getPropertyCSSValue(property).getCssText());
          ps.println("priority: " + styleDeclaration.getPropertyPriority(property));
        }


      }
      ps.println("<--------------------end of rule------------------->");
    }

    out.close();
    stream.close();
    result = true;
  }
  catch (IOException e)
  {
    e.printStackTrace();
  }
  catch (Exception e)
  {
    e.printStackTrace();

  }
  finally
  {
    if (ps != null) ps.close();
  }

  if (result) {
    System.out.println("Successfully parsed " + cssfile);
    return true;
  } else {
    System.out.println("Unable to parse " + cssfile);
    return false;
  }
}

private void parseSAC(String cssFile) throws CSSException
{
  try {
//    BufferedReader reader = new BufferedReader(new StringReader(snippet));
    FileInputStream stream = new FileInputStream(getClass().getResource(cssFile).getFile());
    InputSource source = new InputSource(new InputStreamReader(stream));
    Parser parser = new SACParserCSS2();
//    parser.setDocumentHandler();
    parser.parseStyleSheet(source);
    stream.close();
  } catch (IOException ex) {
    throw new CSSException(ex);
  }
}
}
