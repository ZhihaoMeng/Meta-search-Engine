package common.functions.recommendation.click;

public class LikelihoodSimilarity {

	public static double userSimilarity(int k11, int k12, int k21, int k22){
		
		double p1 = k11 * 1.0 / (k11 + k12);
        double p2 = k21 * 1.0 / (k21 + k22);
        double p = (k11 + k21) * 1.0 / (k11 + k12 + k21 + k22);
        double r1 = k11 * Math.log(p) + k12 * Math.log(1 - p) + k21 * Math.log(p) + k22
                    * Math.log(1 - p);
        double r2 = k11 * Math.log(p1) + k12 * Math.log(1 - p1) + k21 * Math.log(p2) + k22
                    * Math.log(1 - p2);
         
        return 2 * (r2 - r1);
	}

}
