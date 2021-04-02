package br.com.pucrs.src;


public class Aleatorio {
    public static int a = 54564;
    public int c = 31;
    public double mod = Math.pow(2,39)-5;
    public int semente = 7;
    public int size = 1001;

    public double[] geraPseudoAleatorio(){
        double[] x = new double[size];
        x[0] = semente;
        System.out.println(x[0] / mod);

        for(int i = 1; i<size; i++){
            x[i] = ((a*x[i-1] + c) % mod);
            System.out.println(x[i]/mod);
        }

        return x;
    }
}
