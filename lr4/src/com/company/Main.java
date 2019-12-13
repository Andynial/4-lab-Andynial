package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

class Film implements Serializable{
    String name;
    String studio;
    String director;
    String lead;
    int year;

    public Film (String name, String studio, String director, String lead, int year){
        this.name = name;
        this.studio = studio;
        this.director = director;
        this.lead = lead;
        this.year = year;
    }

    boolean SameDirector(Film film){
        if (director.equals(film.director))
            return true;
        return false;
    }

    void PrintFilm(){
        System.out.println("===");
        System.out.println("name: " + name);
        System.out.println("studio: " + studio);
        System.out.println("director: " + director);
        System.out.println("lead: " + lead);
        System.out.println("year: " + year);
    }

    String LeadByName(String name, LinkedList<Film> films){
        for (Film film : films){
            if (film.name.equals(name))
                return film.lead;
        }
        return "Такого фильма нет.";
    }
}

class FileManager{
    File file;

    public FileManager(File file){
        this.file = file;
    }

    void CreateFile() throws IOException {
        if (file.exists()){
            System.out.println("Такой файл существует.");
            Film film1 = new Film("Alien Abduction", "Studio Pictures", "Andy", "Thatone Actorguy", 1999);
            Film film2 = new Film("Can't Find My Face", "Picture Studios", "Andy", "Thatone Actresslady", 2000);
            Film film3 = new Film("I'm On Fire Help", "Fire Presents", "Some Noname", "Andy", 2013);

            try(OutputStream out = new FileOutputStream(file)){
                String str = film1.name + '\n' + film1.studio + '\n' + film1.director + '\n' + film1.lead + '\n' + Integer.toString(film1.year);
                str += "\n===";
                str += '\n' + film2.name + '\n' + film2.studio + '\n' + film2.director + '\n' + film2.lead + '\n' + Integer.toString(film2.year);
                str += "\n===";
                str += '\n' + film3.name + '\n' + film3.studio + '\n' + film3.director + '\n' + film3.lead + '\n' + Integer.toString(film3.year);
                str += "\n===";
                byte[] data = str.getBytes();
                out.write(data);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("Такого файла нет.");
        }
    }

    LinkedList<Film> ReadFromFile() throws IOException {
        LinkedList<Film> films = new LinkedList<Film>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();

        while (line != null) {
            Film film;
            ArrayList<String> props = new ArrayList<String>();
            int year = 0;
            while (!line.equals("===")){
                if (tryParseInt(line))
                    year = Integer.parseInt(line);
                else
                    props.add(line);
                line = reader.readLine();
            }
            film = new Film(props.get(0), props.get(1), props.get(2), props.get(3), year);
            films.add(film);
            line = reader.readLine();
        }

        return films;
    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    String FormLine(String name){ //create array of N spaces, fill it up with needed string, get the remaining blanks, add them when composing main string
        int N = 30; //max length of line
        char[] spaces = new char[N];
        for (char ch : spaces){
            ch = ' ';
        }
        char[] namech = name.toCharArray();
        for (int i = 0; i < name.length(); i++){
            spaces[i] = namech[i];
        }
        String res = "";
        for (char ch : spaces){
            res += ch;
        }
        return res;
    }

    void MakeRAF(String name, LinkedList<Film> films) throws IOException {
        File newfile = new File(name);
        newfile.createNewFile();
        RandomAccessFile raf = new RandomAccessFile(newfile, "rw");
        for (Film film : films){
            String str = FormLine(film.name) +'\n' + FormLine(film.studio) + '\n' + FormLine(film.director)
                    + '\n' + FormLine(film.lead) + '\n' + FormLine(Integer.toString(film.year)) + '\n';
            str += FormLine("===");
            str += '\n';
            raf.writeBytes(str);
        }

        int minyear = 0;
        String content = "";
        for (int i = 0; i < raf.length(); i += 31) {
            raf.seek(i);
            String line = raf.readLine();
            content += line + '\n';
            String str = line.substring(0, 4);
            if (tryParseInt(str)) {
                int year = Integer.parseInt(str);
                if (minyear == 0)
                    minyear = year;
                else if (year < minyear)
                    minyear = year;
            }
        }

        for (int i = 0; i < content.length(); i += 31){
            String line = content.substring(i, i + 30);
            if (line.contains(String.valueOf(minyear))) {
                int down_index = 0;
                String div = "";
                for (int j = i; j < content.length(); j += 31){
                    div = content.substring(j, j + 4);
                    if (div.contains("===")){
                        down_index = j;
                        break;
                    }
                }
                int up_index = 0;
                for (int j = i; j >= 0; j -= 31){
                    div = content.substring(j, j + 4);
                    if (div.contains("===")){
                        up_index = j;
                        break;
                    }
                    up_index = j;
                }

                String before = content.substring(0, up_index);
                String after = content.substring(down_index);
                content = before + after;
            }

        }
        raf.setLength(0);
        raf.writeBytes(content);

        raf.close();
    }
}

class ClassTextFile{
    String path;
    public ClassTextFile(String path){
        this.path = path;
    }

    Film MakeFile(String name) throws FileNotFoundException {
        File file = new File(path + name);
        Film film = null;
        if (file.exists()){
            Scanner in = new Scanner(file);
            ArrayList<String> props = new ArrayList<String>();
            int year = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                if (tryParseInt(line))
                    year = Integer.parseInt(line);
                else
                    props.add(line);
            }
            film = new Film(props.get(0), props.get(1), props.get(2), props.get(3), year);
            return film;
        } else {
            System.out.print("Такого файла не существует.");
            return film;
        }
    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

class ObjectSeries{
    String path;
    ArrayList<Film> films = new ArrayList<Film>();

    public ObjectSeries(String path){
        this.path = path;
    }

    void WriteFile(String name){
        File file = new File(path + name);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
        {
            Film film = new Film("Hope", "Dissociation Studios", "Still Andy", "Also Andy", 2019);
            oos.writeObject(film);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void ReadObject(String name){
        File file = new File(path + name);

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
        {
            Film film = (Film)ois.readObject();
            System.out.printf(film.name);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void MakeCollection(String name){
        File file = new File(path + name);

        Film film1 = new Film("I Finally Lost It", "Your Mom", "Q. Tarantino", "Your Mom", 1988);
        Film film2 = new Film("They Might Be Giants", "Giants Inc.", "John Linnell", "John Flansburg", 2013);

        films.add(film1);
        films.add(film2);

        for (Film film : films){
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
            {
                oos.writeObject(film);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    void ReadCollection(String name) throws FileNotFoundException {
        File file = new File(path + name);
        Scanner in = new Scanner(file);
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
        {
            while (true){
                try {
                    Film film = (Film)ois.readObject();
                    System.out.println("just written into collection: " + film.name);
                    films.add(film);
                } catch (EOFException e){
                    break;
                }
            }

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void Output(String name) throws IOException {
        File file = new File(path + name);
        System.out.println("Содержимое коллекции:");
        for (Film film : films){
            film.PrintFilm();
        }
        System.out.println("Содержимое файла:");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();

        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
    }

    ArrayList<Film> getCollection(){
        return films;
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            Task1();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        String path = "C:\\Users\\User\\Desktop\\Everlasting Hug\\Java\\lr4\\";
        String filename = "TextHere.txt";
        /*System.out.print("Введите имя файла:");
        Scanner in = new Scanner(System.in);
        filename = in.next();*/
        File file = new File(path + filename);
        FileManager manager = new FileManager(file);
        manager.CreateFile();
        LinkedList<Film> films = manager.ReadFromFile();

        for (int i = 0; i < films.size(); i++){
            if (!films.get(i).SameDirector(films.get(i+1))){
                films.remove(films.get(i+1));
            }
        }
        System.out.println("Фильмы одного режиссера:");
        for (Film film : films){
            System.out.println(film.name);
        }

        manager.MakeRAF("NewText.txt", films);

        Task3Ex1(path);
        Task3Ex2(path);

        filename = "ForObject.txt";
        File file2 = new File(path + filename);
        file2.createNewFile();
        ClassTextFile ctf = new ClassTextFile(path);
        ctf.MakeFile(filename);

        filename = "Serialize.txt";
        File file3 = new File(path + filename);
        file3.createNewFile();
        ObjectSeries os = new ObjectSeries(path);
        os.WriteFile(filename);
        os.ReadObject(filename);
        os.MakeCollection(filename);
        os.ReadCollection(filename);
        os.Output(filename);

    }

    static void Task1() throws IOException {
        //excercise 1
        File text1 = new File("C:\\Users\\User\\Desktop\\Everlasting Hug\\Java\\lr4\\MyFile1.txt");
        File text3 = new File("C:\\MyText3\\MyText3.txt");
        File dir = new File("C:\\first\\second\\third");
        text1.createNewFile();
        //File text2 = new File("C:\\MyFile2.txt");
        //text2.createNewFile();
        text3.createNewFile();
        dir.mkdirs();

        //excercise 2
        System.out.println("text1, " + text1.getName() + ", " + text1.getPath());
        System.out.println(text1.length() / (1024 * 1024) + " мб, файл");

        //excercise 3
        File dir2 = new File("C:\\Users\\User\\Desktop\\Everlasting Hug\\Java\\lr4\\MyFolder");
        dir2.mkdir();
        File gendir = new File("C:\\Users\\User\\Desktop\\Everlasting Hug\\Java\\lr4");
        String[] paths = gendir.list();
        for(String path:paths) {
            System.out.println(path);
        }
        File[] files = gendir.listFiles();
        int count = 0;
        for(File fl:files){
            if (fl.isFile())
                System.out.println(fl.getName());
            else
                count++;
        }
        System.out.println("Кол-во папок: " + count);
        text1.delete();
        text3.delete();
        dir.delete();
        dir2.delete();
    }

    static void Task3Ex1(String path) throws IOException {
        String name1 = "T1.txt";
        String name2= "T2.txt";
        File file1 = new File(path + name1);
        File file2 = new File(path + name2);
        file1.createNewFile();
        file2.createNewFile();

        try(BufferedReader reader = new BufferedReader(new FileReader(file1))){
            OutputStream out1 = new FileOutputStream(file1);
            OutputStream out2 = new FileOutputStream(file2);
            String str = "sdfs";
            byte[] data = str.getBytes();
            out1.write(data);
            String towrite = reader.readLine();
            byte[] data1 = towrite.getBytes();
            out2.write(data1);
            out1.close();
            out2.close();
        } catch (IOException e){
            System.out.print(e.getMessage());
        }
    }

    static void Task3Ex2(String path) throws IOException {
        String name1 = "A.txt";
        String name2= "B.txt";
        File file1 = new File(path + name1);
        File file2 = new File(path + name2);
        file1.createNewFile();
        file2.createNewFile();
        String content = "";
        for (int i = 0; i < 512; i++)
            content += "b";
        OutputStream out1 = new FileOutputStream(file1);
        byte[] data = content.getBytes();
        out1.write(data);
        out1.close();

        BufferedReader inb = new BufferedReader(new FileReader(file1), 128);
        BufferedWriter outb = new BufferedWriter(new FileWriter(file2), 128);
        String buf = inb.readLine();
        outb.write(buf);
        outb.close();
        inb.close();
    }
}
