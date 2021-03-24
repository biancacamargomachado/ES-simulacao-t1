package com.pucrs;

public class Main {

    public static void main(String[] args) {
        /**
         * c = é uma constante usada para maior variação
         * dos números gerados
         * a = um número
         * mod = um número grande
         */
        geraPseudoAleatorio(54564, 31, Math.pow(2,39)-5, 7);
    }

    public static void geraPseudoAleatorio(int a, double c, double mod, double semente){
        int size = 1001;
        double[] x = new double[size];
        x[0] = semente;
        System.out.println(x[0] / mod);
        for(int i = 1; i<size; i++){
            x[i] = ((a*x[i-1] + c) % mod);
            System.out.println(x[i]/mod);
        }
    }
}
