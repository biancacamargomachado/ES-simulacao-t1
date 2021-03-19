package com.pucrs;

public class Main {

    public static void main(String[] args) {
        /**
         * c = é uma constante usada para maior variação
         * dos números gerados
         *
         *
         */
        geraPseudoAleatorio(23, 0, 1, 0.12232);
    }

    public static void geraPseudoAleatorio(int a, double c, int mod, double semente){
        int size = 1000;
        double[] x = new double[size];
        x[0] = semente;
        System.out.println(x[0]);
        for(int i = 1; i<size; i++){
            x[i] = (a*x[i-1] + c) % mod;
            System.out.println(x[i]);
        }

        StringBuilder sb=new StringBuilder();
        for (int i=0;i<x.length;i++)
        {
            sb=sb.append(x[i]);
            if(i != x.length-1)
            {
                sb.append(",");
            }
        }
        System.out.println(sb.toString());
    }
}
