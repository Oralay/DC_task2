package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

/**
 * Processor of HTTP request.
 */
public class Processor implements Runnable {
    private final Socket socket;
    private final HttpRequest request;
    private PrintWriter output;

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() throws IOException {
        output = new PrintWriter(socket.getOutputStream());

        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();

        String[] arr = request.getRequestLine().split(" ");
        String[] reqArr = Arrays.stream(arr[1].split("/"))
                .filter(s -> (s != null && s.length() > 0))
                .toArray(String[]::new);

        if (arr[0] == null) { return; }
        if (!Objects.equals(arr[0], "GET")) { return; }

        if (reqArr.length > 0) {
            String action = reqArr[0];
            if (Objects.equals(action, "exec")) {
                HandleTask(reqArr);
            }
        } else {
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>Hello, world!</p></body>");
            output.println("</html>");
        }

        output.flush();
        System.out.flush();

        socket.close();
    }

    private void HandleTask(String[] reqArr) {
        String task = reqArr[1];
        output.println("<html>");
        switch (task) {
            case "prime" -> {
                int primeIndex = Integer.parseInt(reqArr[2]);
                output.println("<head><title>Prime number</title></head>");
                CalculatePrime(primeIndex);
            }
            case "fib" -> {
                int fibIndex = Integer.parseInt(reqArr[2]);
                output.println("<head><title>Fibonacci</title></head>");
                output.println("<body><p>");
                CalculateFibonacci(fibIndex, new FibonacciNumbers(0, 1, 0));
                output.println("</p></body>");
            }
            case "sleep" -> {
                int millis = Integer.parseInt(reqArr[2]);
                output.println("<head><title>Sleep</title></head>");
                Sleep(millis);
            }
        }
        output.println("</html>");
    }

    private void CalculatePrime(int primeIndex) {
        int num = 1, count = 0, i;
        while (count < primeIndex) {
            num = num + 1;
            for (i = 2; i <= num; i++) {
                //determines the modulo and compare it with 0
                if (num % i == 0) {
                    //breaks the loop if the above condition returns true
                    break;
                }
            }
            if (i == num) {
                //increments the count variable by 1 if the number is prime
                count = count + 1;
            }
        }
        output.printf("<body><p>The %dth prime number is %d</p></body>", primeIndex, num);
    }

    private class FibonacciNumbers {
        public BigInteger n1;
        public BigInteger n2;
        public BigInteger n3;

        public FibonacciNumbers(long i, long i1, long i2) {
            n1 = BigInteger.valueOf(i);
            n2 = BigInteger.valueOf(i1);
            n3 = BigInteger.valueOf(i2);
        }
    }

    private void CalculateFibonacci(int count, FibonacciNumbers nums) {
        if (count > 0) {
            nums.n3 = nums.n1.add(nums.n2);
            nums.n1 = nums.n2;
            nums.n2 = nums.n3;
            output.print(" " + nums.n3);
            CalculateFibonacci(count - 1, nums);
        }
    }

    private void Sleep(int millis) {
        output.printf("<body><p>Start Time: %d</p></body>", System.currentTimeMillis());
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        output.printf("<body><p>End Time: %d</p></body>", System.currentTimeMillis());
    }
}