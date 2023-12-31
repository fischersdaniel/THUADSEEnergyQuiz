package thu.adse.energyquiz.QuestionCatalog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import thu.adse.energyquiz.R;

public class QuestionAdapterQuestionCatalog extends RecyclerView.Adapter<QuestionAdapterQuestionCatalog.QuestionViewHolder> {
    private final RecyclerViewInterfaceQuestionCatalog recyclerViewInterface;


    Context context;

    ArrayList<QuestionQuestionCatalog> list;


    public QuestionAdapterQuestionCatalog(Context context, ArrayList<QuestionQuestionCatalog> list,
                           RecyclerViewInterfaceQuestionCatalog recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.question_item_question_catalog,parent,false);
        return  new QuestionViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {

        QuestionQuestionCatalog question = list.get(position);
        holder.questionTitle.setText(question.questionTitle);
        holder.answerText1.setText(question.answer1.answerText);
        holder.answerText2.setText(question.answer2.answerText);
        holder.answerText3.setText(question.answer3.answerText);
        holder.answerText4.setText(question.answer4.answerText);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewInterface != null) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos, question);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder{

        TextView questionTitle, answerText1, answerText2, answerText3, answerText4;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);


            questionTitle = itemView.findViewById(R.id.textViewQuestionTitle);
            answerText1 = itemView.findViewById(R.id.textViewAnswerText1);
            answerText2 = itemView.findViewById(R.id.textViewAnswerText2);
            answerText3 = itemView.findViewById(R.id.textViewAnswerText3);
            answerText4 = itemView.findViewById(R.id.textViewAnswerText4);


            //hier könnte man jetzt die korrekten Antworten markieren im Fragenkatalog

        }
    }
}
