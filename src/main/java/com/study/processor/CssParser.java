package com.study.processor;

import com.steadystate.css.parser.SACParserCSS3;
import com.study.processor.model.Rule;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;

import java.io.IOException;
import java.util.List;

/**
 * aborovyk
 * 04.11.16.
 */
public class CssParser extends SACParserCSS3
{


private static List<Rule> rules;


//----------------------------------------------------------------------

public CssParser()
{
}


//----------------------------------------------------------------------


// XXX i would brefer the parser in an extra class "CssParser" becouse it is completely different functionality than the actual xml "processor"
public static List<Rule> parseCss(InputSource source) {
  Parser parser = new SACParserCSS3();
  parser.setDocumentHandler(new CssProcessor());
  try {
    parser.parseStyleSheet(source);
  } catch (IOException e) {
//    Trace.error("CssProcessor.parseCss: " + e.getMessage());
  }
  return rules;
}


}
