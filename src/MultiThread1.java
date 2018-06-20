import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/*
    單一BufferedReader物件，由多個執行緒使用
 */

public class MultiThread1 {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Set<String> ips = new HashSet<>();
        String filePath = args[0];
        String outputPath = args[1];
        int numberOfThreads = Integer.valueOf(args[2]);

        try (FileReader fr = new FileReader(filePath);
             BufferedReader br = new BufferedReader(fr);
             FileWriter fw = new FileWriter(outputPath);
             BufferedWriter bw = new BufferedWriter(fw)){

            System.out.println("讀取檔案位置: " + args[0]);
            br.readLine();                                                          //removes header line

            List<Callable<Set<String>>> tasks = new ArrayList<>();
            for(int i=0; i<numberOfThreads; i++) {
                tasks.add(new ReadThread1(br));
            }

            ExecutorService exec = Executors.newFixedThreadPool(numberOfThreads);   // multi-threads starts
            List<Future<Set<String>>> futures = exec.invokeAll(tasks);

            for(Future<Set<String>> future: futures) {                              // aggregate returned values
                ips.addAll(future.get());
            }

            long read_end = System.currentTimeMillis();
            long read_duration = read_end - start;
            System.out.println("read_duration: " + read_duration/1000.0 + "s");

            System.out.println("寫入檔案位置: " + args[1]);
            for(String ip2 : ips) {                                                 // writing file
                bw.write(ip2);
                bw.newLine();
            }

            long write_end = System.currentTimeMillis();
            long write_duration = write_end - read_end;
            System.out.println("write_duration: " + write_duration/1000.0 + "s");

            System.out.println("總共有" + ips.size() + "不同ips");                   // number of ips
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        long total_end = System.currentTimeMillis();
        long total_duration = total_end - start;
        System.out.println("Time spent: " + total_duration/1000.0 + "s");
        System.exit(0);
    }
}

class ReadThread1 implements Callable<Set<String>>{
    BufferedReader br;
    Set<String> ips;

    public ReadThread1(BufferedReader br){
        this.br = br;
        this.ips = new HashSet<>();
    }

    @Override
    public Set<String> call() throws Exception {
        String line;
        String ip;

        while((line = br.readLine()) != null) {                 // reading file
            ip = line.split(",")[0];
            ips.add(ip);
        }

        return ips;
    }
}
