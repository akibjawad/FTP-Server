import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ServerTask implements Runnable
{
    Socket ClientSocket;
    int ClientNumber;
    int Clientcount;
    HashMap<String,ConnectionUtilities> ClientList;
    String ClientId;
    Thread ConnectionThread;
    ConnectionUtilities connectionUtilities;
    DataOutputStream outputFromServer;
    DataInputStream inputToServer;
    ObjectInputStream objectInputStream;
    FileDescription fileDescription;
    String temp="Client Number ["+ ClientNumber+ "]";
    int serverSize=10*1024*1024;


    String RecepientId;
    int FileSize;
    String FileType;
    String FileName;
    String FileId;
    HashMap<String,ArrayList<Chunk>> files;
    HashMap<String,String> recepientList;

    HashMap<String,FileDescription> fileDescriptionHashMap =new HashMap<String, FileDescription>();

    ArrayList<Integer> ChunkSizes=new ArrayList<Integer>();

    int chunkSize;
    byte[] buffer;
    int chunkCount;
    Random randomizer = new Random();

    int remaining;
    byte[] extra;
    int complete;
    //Integer complete;
    extra ext;

    public ServerTask(Socket ClientSocket, String clientId, HashMap<String,ConnectionUtilities> ClientList)
    {
        this.ClientSocket=ClientSocket;
        this.ClientId=clientId;
        this.ClientList=ClientList;
        for(int i=1;i<=64;i++)
        {
            ChunkSizes.add(1024*i);
        }
        files=new HashMap<String, ArrayList<Chunk>>();
        try{
            //outputFromServer=new DataOutputStream(this.ClientSocket.getOutputStream());
            //inputToServer=new DataInputStream(this.ClientSocket.getInputStream());
            //objectInputStream=new ObjectInputStream(this.ClientSocket.getInputStream());
            System.out.println("Streams initialization done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ConnectionThread=new Thread(this,temp);
        System.out.println("thread"+ ConnectionThread);
        ConnectionThread.start();
    }
    public ServerTask(String clientId,ConnectionUtilities connectionUtilities, HashMap<String,ConnectionUtilities> clientList,
                      HashMap<String,ArrayList<Chunk>> files, HashMap<String,String> recepientList,
                      HashMap<String,FileDescription> fileDescriptionHashMap,ArrayList<Integer> chunkSizes,extra ext)
    {
        this.ClientId=clientId;
        this.connectionUtilities=connectionUtilities;
        this.ClientList=clientList;
        this.files=files;
        this.recepientList=recepientList;
        this.fileDescriptionHashMap=fileDescriptionHashMap;
        this.ChunkSizes=chunkSizes;
        this.chunkCount=chunkCount;
        this.ext=ext;
        //files=new HashMap<String, ArrayList<Chunk>>();
        //recepientList=new HashMap<String, String>();
        //comlete=0;
        //complete=new Integer(0);
        //ConnectionThread=new Thread(this,temp);
        //ConnectionThread.start();

    }

    @Override
    public void run()
    {
            while (true) {
                //outputFromServer.writeUTF("Do you want to send files?(Y/n)");
                temp = "Do you want to send files?(Y/n)";
                connectionUtilities.write(temp);
                if ((connectionUtilities.read().toString()).equals("y")) {
                    //outputFromServer.writeUTF("Enter recepient id");
                    temp = "Enter Recepient ID:";
                    connectionUtilities.write(temp);
                    //RecepientId = inputToServer.readUTF();
                    RecepientId = connectionUtilities.read().toString();
                    if (ClientList.containsKey(RecepientId)) {
                        //outputFromServer.writeUTF("Enter filename with extention and Filesize in bytes");
                        temp = "Enter filename with extention";
                        connectionUtilities.write(temp);
                        //FileName = inputToServer.readUTF();
                        //FileSize = inputToServer.readInt();
                        //FileType = inputToServer.readUTF();
                        fileDescription = (FileDescription) connectionUtilities.read();
                        //fileDescription = new FileDescription(RecepientId, FileName);
                        FileName = fileDescription.name;
                        FileSize = fileDescription.FileSize;
                        System.out.println(FileName);
                        FileId = ClientId + RecepientId + FileName;
                        recepientList.put(RecepientId, FileId);
                        fileDescriptionHashMap.put(FileId, fileDescription);   //if users goes online
                        FileName = "server/" + FileId;
                        if (usedMemory() + FileSize > serverSize) {
                            //outputFromServer.writeUTF("File is too big to transfer at this moment");
                            connectionUtilities.write("File is too big to transfer at this moment");
                        } else {
                            chunkSize = ChunkSizes.get(randomizer.nextInt(ChunkSizes.size()));
                            System.out.println("Used memory: " + usedMemory());
                            //outputFromServer.writeUTF("Divide your file into chunk of " + chunkSize + " bytes");
                            connectionUtilities.write("Divide your file into chunk of " + chunkSize + " bytes");
                        }
                        //outputFromServer.writeInt(chunkSize);
                        connectionUtilities.write(chunkSize);
                        //outputFromServer.writeUTF(FileId);
                        connectionUtilities.write(FileId);
                        chunkSize = 3072;
                        //FileOutputStream fileOutputStream = new FileOutputStream(FileName);
                        //fileDescription=(FileDescription)objectInputStream.readObject();
                        //fileDescriptionHashMap.put(FileId,fileDescription);
                        chunkCount = (int) Math.ceil((float) FileSize / chunkSize);
                        ext.chunkCount=chunkCount;
                        buffer = new byte[chunkSize];
                        remaining = FileSize;
                            ArrayList<Chunk> file = new ArrayList<>();

                            /*
                            int p;
                            while ((p=(inputToServer.read(buffer)))>0)
                            {
                                System.out.println("Read "+ p +"bytes from client");
                                fileOutputStream.write(buffer);
                            }
                            */
                            try {
                                connectionUtilities.socket.setSoTimeout(30000);
                                for (int i = 0; i < chunkCount; i++) {
                                    if (remaining >= chunkSize) {
                                        //Chunk chunk=(Chunk) objectInputStream.readObject();
                                        Chunk chunk = (Chunk) connectionUtilities.read();
                                        //Chunk chunk = new Chunk(chunkSize, FileId);
                                        //inputToServer.read(buffer);
                                        //chunk.data = buffer;
                                        file.add(chunk);
                                        //fileOutputStream.write(chunk.data);
                                        //outputFromServer.writeUTF(chunkSize + " bytes receieved ");
                                        connectionUtilities.write(chunkSize + " bytes receieved");
                                    } else {
                                        Chunk chunk = (Chunk) connectionUtilities.read();
                                        //Chunk chunk=(Chunk) objectInputStream.readObject();
                                        //Chunk chunk = new Chunk(chunkSize, FileId);
                                        //extra = new byte[remaining];
                                        //inputToServer.read(extra);
                                        //chunk.data = extra;
                                        file.add(chunk);
                                        //fileOutputStream.write(chunk.data);
                                        connectionUtilities.write(chunkSize + " bytes receieved");
                                        //outputFromServer.writeUTF(remaining + " bytes receieved ");
                                    }
                                    remaining = remaining - chunkSize;
                                }
                                files.put(FileId, file);
                            }
                            catch (SocketException e)
                            {
                                System.out.println("Sender Logs Out");
                                file.clear();
                            }
                        //FileOutputStream fileOutputStream = new FileOutputStream(FileName);
                        int receievedSize = 0;
                        ArrayList<Chunk> retreve = files.get(FileId);
                        for (Chunk x : retreve) {
                            //fileOutputStream.write(x.data);
                            receievedSize = receievedSize + x.data.length;
                        }
                        System.out.println("Receieved Size: " + receievedSize + "and File Size: " + FileSize);
                        if (receievedSize == FileSize) {
                            //outputFromServer.writeUTF("Complete File receieved");
                            connectionUtilities.write("----Complete file receieved----");
                            ext.complete=1;
                            /*
                            ConnectionUtilities receiever=ClientList.get(RecepientId);
                            receiever.write("Do you want to receieve a " + FileSize + " byte file from " +ClientId + "?(y/n)");
                            if(receiever.read().toString().equals("y"))
                            {
                                receiever.write("----Starting file transfer----");
                                ArrayList<Chunk> forClient = files.get(FileId);
                                for (Chunk y : forClient) {
                                    receiever.write(y);
                                    receiever.write("Sent " + y.size + " bytes from " + ClientId);
                                }
                                receiever.write("Comlete file sent");

                            }
                            */

                            synchronized (ext)
                            {
                                try {
                                    ext.notifyAll();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            complete=1;
                            //comlete = 1;
                            System.out.println(complete);

                        } else {
                            //outputFromServer.writeUTF("There was an error in file transfer start again");
                            connectionUtilities.write("here was an error in file transfer start again");
                        }

                        System.out.println("File name is " + FileName + "File Id id " + FileId + " File size is " + FileSize + " bytes");

                    }
                    else
                    {
                        //outputFromServer.writeUTF("Recepient is not online currently");
                        connectionUtilities.write("Recepient is not online currently");
                    }
                }
                else
                {
                    connectionUtilities.write("------WAIT------");

                    synchronized (ext)
                    {
                        try {
                            ext.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    connectionUtilities.write(ext.complete);
                    //while (ext.complete != 1) ;
                    connectionUtilities.write(ext.complete);
                    connectionUtilities.write(ext.chunkCount);
                    System.out.println(complete);
                    if(recepientList.containsKey(ClientId))
                    {
                        String fId=recepientList.get(ClientId);
                        FileDescription fd=fileDescriptionHashMap.get(fId);
                        connectionUtilities.write("Do you want to receieve a " + fd.FileSize + " byte file from " + fd.sender + "?(y/n)");
                        if(connectionUtilities.read().toString().equals("y"))
                        {
                            connectionUtilities.write("----Starting file transfer----");
                            connectionUtilities.write(fId);
                            ArrayList<Chunk> forClient = files.get(fId);
                            for (Chunk y : forClient) {
                                connectionUtilities.write(y);
                                connectionUtilities.write("Sent " + y.size + " bytes from " + ClientId);
                            }
                            connectionUtilities.write("Comlete file sent");

                        }


                    }
                }
            }
        }
    public void filTransmission()
    {

    }
    int usedMemory()
    {
        int size=0;
        for (ArrayList<Chunk> x: files.values())
        {
            if(x.size()!=0)
            {
                size=size+x.size()*x.get(0).size;
                System.out.println(size);
            }
        }
        return size;
    }
    void cancel()
    {

    }
}
