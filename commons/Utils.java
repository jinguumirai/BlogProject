package commons;

import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Utils
{
    public static String readLine(DataInputStream dataInputStream)
    {
        var byteList = new ArrayList<Byte>();
        boolean isNull = false;
        try
        {
            byte tempByte;
            while ((tempByte = dataInputStream.readByte()) != 10)
            {
                if (tempByte != 13) {
                    byteList.add(tempByte);
                }
            }
        }
        catch (Exception e)
        {
            isNull = true;
        }

        if (isNull && byteList.isEmpty())
        {
            return null;
        }
        else
        {
            byte[] byteArray = new byte[byteList.size()];
            for (int i = 0; i < byteArray.length; i++)
            {
                byteArray[i] = byteList.get(i);
            }
            return new String(byteArray, StandardCharsets.UTF_8);
        }
    }
}