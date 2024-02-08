package thu.adse.energyquiz.QuestionCatalog;

/**
 * An interface to represent the RecyclerView of the question catalog.
 * This interface is used to handle the click events of the RecyclerView in the question catalog.
 * @author Sebastian Steinhauser
 */
public interface RecyclerViewInterfaceQuestionCatalog {
    void onItemClick(int position, QuestionQuestionCatalog question);
}
