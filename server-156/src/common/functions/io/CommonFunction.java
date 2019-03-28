package common.functions.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CommonFunction 
{
	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * @param 文件地址名称
	 * @return
	 */
    public static ArrayList<String> readFileByLines(String fileName) 
    {
        File file = new File(fileName);
        BufferedReader reader = null;
        ArrayList<String> list = new ArrayList<String>();
        try 
        {
        	System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString=reader.readLine())!=null) 
            {
            	// 显示行号
                list.add(tempString);
               // line++;
            }
            reader.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (reader != null) 
            {
                try 
                {
                    reader.close();
                } 
                catch (IOException e1) 
                {
                	//Do nothing!
                }
            }
        }
        return list;
    }
	
    /**
	 * 往文件filename中写入字符串output
	 * @param filename 文件的名称
	 * @param output 字符串的名称
     * @param linenumber 
     * @param count 
	 */
    public static void writeFile(String filename,String output,double rate, int linenumber)
    {
		FileWriter writer=null;
		try 
		{
			writer=new FileWriter(filename, true);
			writer.write(output+" "+rate+" "+linenumber+"\r\n");
			writer.flush();
		} 
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (writer!= null) {
				try 
				{
					writer.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
    
    /**
   	 * @param 
   	 * @return
   	 */
   	public static boolean exists(String URLName) 
   	{

   	    try
   	    {
   	          HttpURLConnection.setFollowRedirects(false);
   	          HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
   	          con.setRequestMethod("HEAD");
   	          return (con.getResponseCode()==HttpURLConnection.HTTP_OK);
   	     }
   	    catch (Exception e)
   	    {
   	           e.printStackTrace();
   	           return false;

   	    }
   	}

	public void writeFile(String filename, String a) {
		// TODO Auto-generated method stub
		FileWriter writer=null;
		try 
		{
			writer=new FileWriter(filename, true);
			writer.write(a+"\r\n");
			writer.flush();
		} 
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (writer!= null) {
				try 
				{
					writer.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}