/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cda.cache;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pdpi
 */
public class CachedParam implements Serializable
{

  private long id;
  private String name, value;


  public CachedParam()
  {
  }


  public CachedParam(String name, String value)
  {
    this.name = name;
    this.value = value;
  }


  CachedParam(JSONObject json) throws JSONException
  {
    this.name = json.getString("name");
    this.value = json.getString("value");
  }


  public long getId()
  {
    return id;
  }


  public void setId(long id)
  {
    this.id = id;
  }


  public String getName()
  {
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getValue()
  {
    return value;
  }


  public void setValue(String value)
  {
    this.value = value;
  }
}
