package com.study.processor;

import com.study.processor.model.Rule;
import org.w3c.css.sac.InputSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * aborovyk
 * 03.11.16.
 */
public final class CssProcessor
{

private static final String PATH = "./src/main/resources/android.css";

public CssProcessor()
{
}


//----------------------------------------------------------------------


public static void main(String[] args)
{
//  try (FileInputStream stream = new FileInputStream(args[0])) {
  try (FileInputStream stream = new FileInputStream(PATH)) {
    InputSource source = new InputSource(new InputStreamReader(stream));
    CssParser parser = new CssParser();
    List<Rule> result = parser.parseCss(source);
    for (Rule rule : result) {
      System.out.println(rule);
    }
  } catch (FileNotFoundException e) {
    System.out.println("CssProcessor.main: " + PATH + " not found");
  } catch (IOException e) {
    System.out.println("CssProcessor.main: " + e.getMessage());
  }
}

}