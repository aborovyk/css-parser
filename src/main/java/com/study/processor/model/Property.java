// Property.java
//
//
// 161023 sd v
//
//


package com.study.processor.model;


// BESCHREIBUNG
//
public final class Property
{

protected String name;

public String getName()
{
  return name;
}

public void setName(String name)
{
  this.name = name;
}

protected String value;

public String getValue()
{
  return value;
}

public void setValue(String value)
{
  this.value = value;
}

protected int specificity;

public int getSpecificity()
{
  return specificity;
}

public void setSpecificity(int specificity)
{
  this.specificity = specificity;
}
//----------------------------------------------------------------------


public Property()
{
}


public Property(String name, String value)
{
  this.name = name;
  this.value = value;
}


@Override
public String toString()
{
  return "Property{" +
          "name='" + name + '\'' +
          ", value='" + value + '\'' +
          ", specificity=" + Integer.toString(specificity, 16) +
          '}';
}

}
