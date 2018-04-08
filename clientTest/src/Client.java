import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    String ClientId,RecepientID;
    int FileSize;
    String choice;
    Scanner ConsoloseInput;
    String FileName;
    int chunkSize;
    int chunkCount=0;
    byte[] buffer;
    int remaining;
    byte[] extra;
    String FileId;
    File file;
    String FilePath="/home/jawad/Desktop/IdeaProjects/server/src/MultiThread.java";
    Socket ClientSocket;
    DataOutputStream outoutToServer;
    DataInputStream inputFromServer;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    ConnectionUtilities connectionUtilities;
    String temp;
    public Client()
    {
        ConsoloseInput=new Scanner(System.in);
        connectionUtilities=new ConnectionUtilities("localhost",55555);
        //objectOutputStream=connectionUtilities.objectOutputStream;
        //objectInputStream=connectionUtilities.objectInputStream;
        try {
            /*
            ClientSocket=new Socket("localhost",55555);
            inputFromServer = new DataInputStream(ClientSocket.getInputStream());
            outoutToServer=new DataOutputStream(ClientSocket.getOutputStream());
            */
            //outoutToServer.writeUTF("Hey i want to connect");

            String msg="Hey i want to connect";
            connectionUtilities.write(msg);
            //ClientSocket.setSoTimeout(30000);
            //objectInputStream=new ObjectInputStream(ClientSocket.getInputStream());
            //objectOutputStream = new ObjectOutputStream(ClientSocket.getOutputStream());

            //System.out.println("Enter user ID:");
            //System.out.println(objectInputStream.read());
            //System.out.println(objectInputStream.read());
                System.out.println(connectionUtilities.read().toString());
                ClientId = ConsoloseInput.nextLine();
                System.out.println(ClientId);
            //outoutToServer.writeUTF(ClientId);
                connectionUtilities.write(ClientId);
            //System.out.println(inputFromServer.readUTF());
                System.out.println(connectionUtilities.read().toString());//Successfully connected
                while (true) {
                //System.out.println(inputFromServer.readUTF());
                System.out.println(connectionUtilities.read().toString());//send or receieve
                choice = ConsoloseInput.nextLine();
                //outoutToServer.writeUTF(choice);
                connectionUtilities.write(choice);
                if (choice.equals("y"))
                {
                    //System.out.println(inputFromServer.readUTF());
                    System.out.println(connectionUtilities.read().toString());
                    RecepientID = ConsoloseInput.nextLine();
                    //System.out.println(FileSize);
                    //outoutToServer.writeUTF(RecepientID);
                    connectionUtilities.write(RecepientID);
                    //System.out.println(inputFromServer.readUTF());
                    String a=connectionUtilities.read().toString(); //recepient online or not
                    if(a.equals("Enter filename with extention"))
                    {
                        System.out.println(a);
                        FileName = ConsoloseInput.nextLine();
                        FilePath = FileName;
                        file = new File(FilePath);
                        FileName = file.getName();
                        System.out.println(FileName);
                        FileSize = (int) file.length();
                        System.out.println(file.length() + " in integer " + FileSize);
                        //FileDescription fileDescription=new FileDescription(RecepientID,FileName);
                        //outoutToServer.writeUTF(FileName);
                        //outoutToServer.writeInt(FileSize);
                        //outoutToServer.writeUTF(Files.probeContentType(file.toPath()));
                        FileDescription fileDescription = new FileDescription(ClientId,RecepientID, FileName, FileSize);
                        connectionUtilities.write(fileDescription);
                        //System.out.println(inputFromServer.readUTF());
                        String big=connectionUtilities.read().toString();
                        if(big.equals("File is too big to transfer at this moment"))
                        {
                            System.out.println(big);
                            continue;
                        }
                        //chunkSize = inputFromServer.readInt();
                        System.out.println(big); //not larger than memory
                        chunkSize = (int) connectionUtilities.read();
                        System.out.println("Chunksize is " + chunkSize);
                        //FileId = inputFromServer.readUTF();
                        FileId = connectionUtilities.read().toString();
                        chunkSize = 3 * 1024;
                        //objectOutputStream.writeObject(fileDescription);
                        FileInputStream fileInputStream = new FileInputStream(FilePath);
                        System.out.println(chunkSize);
                        chunkCount = (int) Math.ceil((float) FileSize / chunkSize);
                        System.out.println(chunkCount);
                        buffer = new byte[chunkSize];
                        remaining = FileSize;
                /*
                String reply;
                int x;
                while ((x=(fileInputStream.read(buffer)))>0)
                {
                    System.out.println("Read "+ x +" bytes");
                    outoutToServer.write(buffer);
                }
                */

                        for (int i = 0; i < chunkCount; i++) {
                            if (remaining >= chunkSize) {
                                Chunk chunk = new Chunk(chunkSize, FileId);
                                fileInputStream.read(chunk.data);
                                //chunk.data=buffer;
                                //outoutToServer.write(buffer);
                                //outoutToServer.write(buffer);
                                //objectOutputStream.writeObject(chunk);
                                connectionUtilities.write(chunk);
                                //System.out.println(inputFromServer.readUTF());
                                System.out.println(connectionUtilities.read().toString());
                            } else {
                                extra = new byte[remaining];
                                Chunk chunk = new Chunk(remaining, FileId);
                                fileInputStream.read(chunk.data);
                                connectionUtilities.write(chunk);
                                //Chunk chunk=new Chunk(remaining,FileId);
                                //objectOutputStream.writeObject(chunk);
                                //outoutToServer.write(extra);
                                //outoutToServer.write(buffer,0,remaining);
                                //System.out.println(inputFromServer.readUTF());
                                System.out.println(connectionUtilities.read().toString());
                            }
                            remaining = remaining - chunkSize;
                        }

                        //System.out.println(inputFromServer.readUTF());
                        String temp = connectionUtilities.read().toString();
                        //if (temp.equals("----Complete file receieved----")) {
                        System.out.println(temp);
                        //}
                    }
                    else
                    {
                        System.out.println(connectionUtilities.read().toString());
                    }
                }
                else
                {
                    /*
                    System.out.println("----wait----");
                    System.out.println(connectionUtilities.read().toString());
                    connectionUtilities.write(ConsoloseInput.nextLine());
                    System.out.println(connectionUtilities.read().toString());
                    FileOutputStream fileOutputStream=new FileOutputStream("Receieved/test");
                    for (int i = 0; i <chunkCount ; i++) {
                        Chunk y=(Chunk) connectionUtilities.read();
                        fileOutputStream.write(y.data);
                        System.out.println(connectionUtilities.read().toString());
                    }
                    System.out.println(connectionUtilities.read().toString());
                    */

                    System.out.println(connectionUtilities.read().toString());//wait
                    System.out.println((int)connectionUtilities.read());
                    System.out.println((int)connectionUtilities.read());
                    chunkCount=(int)connectionUtilities.read();
                    System.out.println(connectionUtilities.read().toString());//prompt
                    connectionUtilities.write(ConsoloseInput.nextLine());
                    System.out.println(connectionUtilities.read().toString());//starting
                    FileId=connectionUtilities.read().toString();
                    FileOutputStream fos=new FileOutputStream("Receieved/FileId");
                    for (int i = 0; i <chunkCount ; i++) {
                        Chunk y=(Chunk) connectionUtilities.read();
                        fos.write(y.data);
                        System.out.println(connectionUtilities.read().toString());
                    }
                    System.out.println(connectionUtilities.read().toString());

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
