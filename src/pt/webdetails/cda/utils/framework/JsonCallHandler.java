package pt.webdetails.cda.utils.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IParameterProvider;

/**
 * Boilerplate for a JSON content generator
 */
public class JsonCallHandler {

  protected static Log logger = LogFactory.getLog(JsonCallHandler.class);
  
  public static final String ENCODING = "UTF-8";
  public static final int INDENT_FACTOR = 2;
  
  private HashMap<String, Method> methods = new HashMap<String, Method>();
  private String methodParameter = "method";
  private String defaultMethod = null;
  
  public static class JsonResultFields 
  {
    public static final String STATUS = "status";
    public static final String ERROR_MSG = "errorMsg";
    public static final String RESULT = "result";
  }
  
  public enum ResponseStatus{
    
    OK,
    ERROR;
    
    @Override public String toString(){
      return super.toString().toLowerCase();
    }
  }
 
  
  public static abstract class Method {
    
    private String name;
    
    public abstract JSONObject execute(IParameterProvider params) throws Exception;
    
    public void setName(String name){
      this.name = name;
    }
    public String getName(){
      return name;
    }
  }

  /**
   * 
   * @param name
   * @param method
   */
  public void registerMethod(String name, Method method){
    method.setName(name);
    methods.put(name, method);
  }
  
  public void handleCall(IParameterProvider requestParams, OutputStream out)
  {
    
    String methodName = requestParams.getStringParameter(methodParameter, defaultMethod);
       
    JSONObject result = null;
    Method method = methods.get(methodName);
    
    try 
    {
      if(methodName == null){
        result = getErrorJson("No method received.");
      }
      else if(method == null)
      {
        result = getErrorJson(MessageFormat.format("Method {0} not found.", methodName));
      }
      else 
      {
        try
        {
          result = method.execute(requestParams);
        }
        catch(JSONException e){
          logger.error( MessageFormat.format("Error building JSON response in method {0}.", methodName) , e);
        }
        catch(Exception e){
          logger.error( MessageFormat.format("Error executing method {0}.", methodName), e);
          result = createJsonResultFromException(e);
        }
      }
      out.write(result.toString(INDENT_FACTOR).getBytes(ENCODING));
      
    } catch (JSONException e) {
      logger.error("Error building JSON response", e);
    } catch (UnsupportedEncodingException e) {
      logger.error("Error attempting to use UTF-8 encoding", e);
    } catch (IOException e) {
      logger.error("Error writing to output stream", e);
    }
  }
  
  public static JSONObject createJsonResultFromException(Exception exc) throws JSONException
  {
    JSONObject result = new JSONObject();
    
    result.put(JsonResultFields.STATUS, ResponseStatus.ERROR.toString());
    result.put(JsonResultFields.ERROR_MSG, exc.getMessage());
    
    return result;
  }
  
  public static JSONObject getErrorJson(String msg) throws JSONException {
    JSONObject result = new JSONObject();
    result.put(JsonResultFields.STATUS, ResponseStatus.ERROR.toString());
    result.put(JsonResultFields.ERROR_MSG, msg);
    return result;
  }


  
}
