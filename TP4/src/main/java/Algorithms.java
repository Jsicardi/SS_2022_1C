public abstract class Algorithms {

    private Algorithms(){

    }

    public static double beemanX(double x, double v, double a, double aPrev, double t, double deltaT){
        return x + v * deltaT + (double)2/3*a*Math.pow(deltaT, 2)-(double)1/6*aPrev*Math.pow(deltaT, 2);
    }

    public static double beemanPredictionV(double v, double a, double aPrev, double deltaT){
        return v + (double)3/2*a*deltaT-0.5*aPrev*deltaT;
    }

    public static double beemanV(double v, double aPrev,double a, double aNext, double deltaT){
        return v + (double)1/3 * aNext * deltaT + (double)5/6 * a * deltaT - (double)1/6 * aPrev* deltaT;
    }

    public static double eulerX(double x, double v, double deltaT){
        return x + v * deltaT;
    }

    public static double eulerV(double v, double f, double m, double deltaT){
        return v + deltaT * f / m;
    }

    public static void gearPredictorPredictDerivatives(double[] oldDer, double[] newDer, double deltaT){
        newDer[4] = oldDer[4] +oldDer[5] * deltaT;
        newDer[3] = oldDer[3] +oldDer[4] * deltaT + oldDer[5] * Math.pow(deltaT, 2) / 2;
        newDer[2] = oldDer[2] +oldDer[3] * deltaT + oldDer[4] * Math.pow(deltaT, 2) / 2 + oldDer[5] * Math.pow(deltaT, 3) / 6;
        newDer[1] = oldDer[1] +oldDer[2] * deltaT + oldDer[3] * Math.pow(deltaT, 2) / 2 + oldDer[4] * Math.pow(deltaT, 3) / 6 + oldDer[5] * Math.pow(deltaT, 4) / 24;
        newDer[0] = oldDer[0] +oldDer[1] * deltaT + oldDer[2] * Math.pow(deltaT, 2) / 2 + oldDer[3] * Math.pow(deltaT, 3) / 6 + oldDer[4] * Math.pow(deltaT, 4) / 24 + oldDer[5] * Math.pow(deltaT, 5) / 120;
    }
}
