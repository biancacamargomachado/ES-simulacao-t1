package br.com.pucrs.src;


public class Aleatorio {
    /**
     * c = é uma constante usada para maior variação
     * dos números gerados
     * a = um número
     * mod = um número grande
     */
    public static int a;
    public int c;
    public double mod;
    public int semente;
    public int size;

    public Aleatorio() {
        this.a = 54564;
        this.c = 31;
        this.mod = Math.pow(2,39)-5;
        this.semente = 7;
        this.size = 1001;
    };

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
