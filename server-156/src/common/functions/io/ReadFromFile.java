package common.functions.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * 从文件读取数据
 * 首先将每行的数据通过空格进行划分，划分后得到的每部分数据都存入LinkedList<String>中
 * 然后将所有的行添加到LinkedList<String>中，最后返回LinkedList<LinkedList<String>>
 * 类型的数据
 * @author Administrator
 *
 */

public class ReadFromFile
{
	public static LinkedList<LinkedList<String>> getInfo(String fileName)
	{
		LinkedList<LinkedList<String>> result = new LinkedList<LinkedList<String>>();
		File file = new File(fileName);
		
        BufferedReader reader = null;
        try
        {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            
//            int line = 0;
            // 一次读入一行，直到读入null为文件结束
            while((tempString = reader.readLine()) != null)
            {
                // 显示行号
//                System.out.println("line " + line + ": " + tempString);
                String[] tempArray = tempString.split("\\s+");
                LinkedList<String> temp = new LinkedList<String>();
                
                for(int i = 0;i < tempArray.length;i ++)
                {
                	temp.add(tempArray[i]);
                }
                
                result.add(temp);
//                line++;
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(reader != null)
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
		
		return result;
	}
	
	public static void main(String[] args)
	{
		LinkedList<LinkedList<String>> result = ReadFromFile.getInfo("file.txt");
		
		for(LinkedList<String> element : result)
		{
			for(int i = 0;i < element.size();i ++)
			{
				System.out.print(element.get(i) + " ");
			}
			System.out.println();
		}
		
	}
}