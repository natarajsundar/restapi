package com.nataraj.code.properties.restservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.nataraj.code.interfaces.Observer;
import com.nataraj.code.interfaces.Subject;

@Path("properties")
public class PropertiesService implements Subject
{
	private ArrayList<Observer> observers;
    private ArrayList<PropertyObject> propertyObjects;

    public static Map<String, String> properties = new TreeMap<String, String>();
   
   private Connection getDBConnection() throws ClassNotFoundException, SQLException
   {
	   Class.forName("org.postgresql.Driver");
	   Connection connection = null;
	   connection = DriverManager.getConnection(
			"jdbc:postgresql://127.0.0.1:5432/strongview", "strongview",
			"");
	   return connection;
   }

   @Path("/{property}")
   @PUT
   @Produces("application/xml")
   public String create(@PathParam("property") String propertyName, @QueryParam("property_value") String propertyValue)
   {
	   
	   System.out.println(propertyName+" : "+propertyValue);
	   properties.put(propertyName, propertyValue);
	   StringBuilder response = new StringBuilder();
	   PreparedStatement pst = null;
	    try
	   {
		   Connection connection = getDBConnection();
		   String stm = "INSERT INTO property(propertyName, propertyvalue) VALUES(?, ?)";
           pst = connection.prepareStatement(stm);
           pst.setString(1, propertyName);
           pst.setString(2, propertyValue);                    
           synchronized(this.getClass())
    	   {
        	   pst.executeUpdate();
    	   }
           notifyObserver();
           connection.close();

	   }
	   catch (SQLException e) 
	   {
		   response.append("<Error> SQL Exception");
		   response.append("</Error>");
		   return new String(response);
		}
	   catch (ClassNotFoundException e) 
	   {
		   response.append("<Error> JDBC Class Not Found");
		   response.append("</Error>");
		   return new String(response);

		}
	   response.append("<Status>200</Status>\n");
	   response.append("<Message>All ok</Message>\n");
	   response.append("<PropertyName>"+propertyName+"</PropertyName>" );
	   return new String(response);
   }

   @Path("/{property}")
   @GET
   @Produces("application/xml")
   public String find(@PathParam("property") String propertyName)
   {
	   StringBuilder response = new StringBuilder();
	   
	   try
	   {
		   Connection dbConnection = getDBConnection();
		   Statement statement = null;

		   String selectTableSQL = "select * from property where propertyname='"+propertyName+"'"; 
		   statement = dbConnection.createStatement();
		   ResultSet rs = statement.executeQuery(selectTableSQL);
		   
		   if (rs.next())
		   {
			   String propertyname = rs.getString("propertyname");
			   String propertyvalue = rs.getString("propertyvalue");
			   response.append("<Property>\n");
			   response.append("<PropertyName>"+propertyName);
			   response.append("</PropertyName>\n");
			   response.append("<PropertyValue>"+propertyvalue);
			   response.append("</PropertyValue>\n");
			   response.append("</Property>");
			   return new String(response);

			}
		   else
		   	{
			   response.append("<Error> Not Found");
			   response.append("</Error>");
			   return new String(response);
		   	}
		 }
	   catch (SQLException e) 
	   {
		   
		   response.append("<Error> SQL Exception");
		   response.append("</Error>");
		   return new String(response);
		} 
	   catch (ClassNotFoundException e) 
	   {
		   response.append("<Error> JDBC class not found.");
		   response.append("</Error>");
		   return new String(response);
	   }
   }

   @Path("/list")
   @GET
   @Produces("application/xml")
   public String list()
   {
	   StringBuilder response = new StringBuilder();
	   response.append("<Properties>\n");
	   try
	   {
		   Connection dbConnection = null;
		   Statement statement = null;

		   String selectTableSQL = "select * from property"; 
		   dbConnection = getDBConnection();
		   statement = dbConnection.createStatement();
		   ResultSet rs = statement.executeQuery(selectTableSQL);
		  
		   while (rs.next())
		   {
			   String propertyname = rs.getString("propertyname");
			   String propertyvalue = rs.getString("propertyvalue");
			   response.append("<Property>\n");
			   response.append("<PropertyName>"+propertyname);
			   response.append("</PropertyName>\n");
			   response.append("<PropertyValue>"+propertyvalue);
			   response.append("</PropertyValue>\n");
			   response.append("</Property>\n");

			}
		 }
	   catch (SQLException e) 
	   {
		   
		   response.append("<Error> SQL Exception");
		   response.append("</Error>");
		   return new String(response);
		} 
	   	catch (ClassNotFoundException e) 
	   	{
			   response.append("<Error> JDBC class not found.");
			   response.append("</Error>");
			   return new String(response);
		}
	   response.append("</Properties>");
	   return new String(response);
   }

   @Override
   public void register(Observer newObserver) {
        observers.add(newObserver);
    }

   public void notifyObserver() {
	           for(Observer observer : observers)
	           {
	               observer.update(list());
	           }
   }
   
   @Override
   public void unregister(Observer deleteObserver) 
   {
	        int observerIndex = observers.indexOf(deleteObserver);
	        // Print out message (Have to increment index to match)
	        System.out.println("Observer " + (observerIndex+1) + " deleted");
	        // Removes observer from the ArrayList
	        observers.remove(observerIndex);
   }
   
   class PropertyObject
   {
	   String name;
	   String value;
	}
}