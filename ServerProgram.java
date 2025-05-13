import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import static java.lang.System.out;
import static commons.Utils.readLine;
import utils.ReadCSV;

public class ServerProgram
{
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args)
    {
        int port = 8081;
        try (var serverSocket = new ServerSocket(port)) {
            out.println("Server program start, listening port " + port);
            DataInputStream reader;
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                out.println("Client connect: " + clientSocket.getRemoteSocketAddress());

                var outputStream = clientSocket.getOutputStream();
                var inputStream = clientSocket.getInputStream();

                reader = new DataInputStream(inputStream);
                String firstLine = readLine(reader);
                String line = readLine(reader);
                String loginID = null;
                while (line != null && !line.isEmpty())
                {
                    var dictElement = line.split(":");
                    if (dictElement[0].equalsIgnoreCase("cookies"))
                    {
                        loginID = dictElement[1];
                    }
                    line = readLine(reader);
                }

                if (firstLine != null && firstLine.contains("GET")) {
                    doGet(firstLine, outputStream, loginID);
                }
                else
                {
                    doPost(outputStream, reader);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void doPost(OutputStream outputStream, DataInputStream reader)
    {
        int contentLen = 0;
        try{
            String line;
            while ((line = readLine(reader)) != null)
            {
                if (line.isEmpty())
                {
                    break;
                }
                else if (line.contains("Content-Length"))
                {
                    var tempStr = line.substring(line.indexOf("Content-Length") + 16);
                    contentLen = Integer.parseInt(tempStr);
                }
            }

            byte[] buf;
            if (contentLen != 0)
            {
                int size = 0;
                buf = new byte[contentLen];
                while (size < contentLen)
                {
                    buf[size++] = reader.readByte();
                }
                PrintWriter writer = getPrintWriter(outputStream, buf, size);
                writer.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    private static PrintWriter getPrintWriter(OutputStream outputStream, byte[] buf, int size) {
        String newStr = new String(buf, 0, size);
        var elements = newStr.split("&");
        String userName = null;
        for (var element: elements)
        {
            var keys = element.split("=");
            if (keys[0].equalsIgnoreCase("username"))
            {
                userName = keys[1];
            }
        }
        return getPrintWriter(outputStream, userName);
    }

    private static PrintWriter getPrintWriter(OutputStream outputStream, String userName) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream,
                StandardCharsets.UTF_8), true);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Access-Control-Allow-Origin: *");
        writer.println("Content-Type: text/plain; CharSet=utf-8");
        if (userName != null)
        {
            writer.println("Set-Cookie: user=" + userName + "; Expires=Wed, 30 Apr 2025 00:00:00 GMT; Domain=localhost");
        }
        writer.println();
        writer.println("userName");
        return writer;
    }

    private static void doGet(String firstLine, OutputStream outputStream, String loginID)
    {
        out.println("Request Line: " + firstLine);
        String[] parts = firstLine.split(" ");
        if (parts.length > 0 && parts[0].equalsIgnoreCase("GET")) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream,
                    StandardCharsets.UTF_8), true);
            writer.println("HTTP/1.1 200 OK");
            writer.println("Access-Control-Allow-Origin: *");
            if (parts[1].equalsIgnoreCase("/")) {
                writer.println("Content-Type: text/html; CharSet=utf-8");
                // writer.println("Set-Cookie: user=zhang; Expires=Wed, 01 Jan 2025 00:00:00 GMT; Domain=localhost");
                writer.println();
                try (var fileInputStream = new FileInputStream("htmlFiles/login.html")) {
                    StringBuilder fileBuilder = getStringBuilder(fileInputStream);
                    writer.println(fileBuilder);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else {
                String resourceName = parts[1];
                String relativeName = resourceName.substring(1);

                String suffix = relativeName.substring(relativeName.lastIndexOf('.') + 1);
                if (suffix.equalsIgnoreCase("ico")) {
                    writer.println("Content-Type: image/x-icon");
                } else if (suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg") ||
                        suffix.equalsIgnoreCase("gif") || suffix.equalsIgnoreCase("png")) {
                    writer.println("Content-Type: image/" + suffix);
                } else if (suffix.equalsIgnoreCase("css")) {
                    writer.println("Content-Type: text/" + suffix + "; CharSet=utf-8");
                }
                else if (suffix.equalsIgnoreCase("js"))
                {
                    writer.println("Content-Type: text/" + "javascript" + "; CharSet=utf-8");
                }
                else if (suffix.equalsIgnoreCase("html"))
                {
                    writer.println("Content-Type: text/" + "html" + "; CharSet=utf-8");
                }
                writer.println();
                try (var imageInputStream = new FileInputStream(relativeName)) {
                    int byteRead;
                    while ((byteRead = imageInputStream.read()) != -1) {
                        outputStream.write(byteRead);
                    }
                } catch (IOException e) {
                    out.println(e.getMessage());
                }
            }
            writer.close();
        }
    }

    private static StringBuilder getStringBuilder(FileInputStream fileInputStream) throws IOException {
        var fileInputReader = new InputStreamReader(fileInputStream);
        var fileReader = new BufferedReader(fileInputReader);
        String fileLine;

        StringBuilder fileBuilder = new StringBuilder();
        while ((fileLine = fileReader.readLine()) != null)
        {
            fileBuilder.append(fileLine);
            fileBuilder.append(System.lineSeparator());
        }
        fileReader.close();
        fileInputReader.close();
        return fileBuilder;
    }
}