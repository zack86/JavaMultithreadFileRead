import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/*
    多個執行緒，各自產生自己的BufferedReader自行使用
 */

public class MultiThread2 {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Set<String> ips = new HashSet<>();
        String filePath = args[0];
        String outputPath = args[1];
        int numberOfThreads = Integer.valueOf(args[2]);

        try (FileWriter fw = new FileWriter(outputPath);
             BufferedWriter bw = new BufferedWriter(fw)){

            List<Callable<Set<String>>> tasks = new ArrayList<>();
            for(int i=0; i<numberOfThreads; i++) {
                tasks.add(new ReadThread2(filePath, i, numberOfThreads));
            }

            ExecutorService exec = Executors.newFixedThreadPool(numberOfThreads);         //multi-thread starts
            List<Future<Set<String>>> futures = exec.invokeAll(tasks);
            for(Future<Set<String>> future: futures) {
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

class ReadThread2 implements Callable<Set<String>>{
    String filePath;
    Set<String> ips;
    int num;
    int numberOfThreads;

    public ReadThread2(String filePath, int num, int numberOfThreads){
        this.filePath = filePath;
        this.ips = new HashSet<>();
        this.num = num;
        this.numberOfThreads = numberOfThreads;
        System.out.println("Starting thread-" + num);
    }

    @Override
    public Set<String> call() throws Exception {
        String line;
        String ip;
        int counter = 0;

        try (FileReader fr = new FileReader(filePath);
             BufferedReader br = new BufferedReader(fr)){

            System.out.println("讀取檔案位置: " + filePath);
            br.readLine();                                          // removes header line

            while(true) {                                           // reading file
                if(counter % numberOfThreads == num) {                            // 4 = number of threads
                    if((line = br.readLine()) != null) {
                        ip = line.split(",")[0];
                        ips.add(ip);
                    } else {
                        break;
                    }
                } else {
                    br.readLine();                                  // discard unused line
                }
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ips;
    }
}
