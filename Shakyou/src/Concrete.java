
public class Concrete {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Movie movie1 = new Movie("MATLIX", Movie.REGULAR);
		Movie movie2 = new Movie("Summer wars", Movie.CHILDRENS);
		Movie movie3 = new Movie("NEWER", Movie.NEW_RELEASE);
		
		Rental rental1 = new Rental(movie1, 3);
		Rental rental2 = new Rental(movie2, 4);
		Rental rental3 = new Rental(movie3, 5);
		
		Customer youbun1 = new Customer("John");
		youbun1.addRental(rental1);
		youbun1.addRental(rental2);
		youbun1.addRental(rental3);
		
		System.out.println(youbun1.statement());

	}

}
