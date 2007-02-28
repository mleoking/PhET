/**
 * Class: Exercise
 * Class: edu.colorado.games4education.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:24:53 PM
 */
package edu.colorado.games4education.exercise;

public class Exercise {
    private String question;
    private Answer[] choices;
    private Answer correctAnswer;

    public Exercise() {
    }

    public Exercise( String question, Answer[] choices, Answer correctAnswer ) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion( String question ) {
        this.question = question;
    }

    public Answer[] getChoices() {
        return choices;
    }

    public void setChoices( Answer[] choices ) {
        this.choices = choices;
    }

    public Answer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer( Answer correctAnswer ) {
        this.correctAnswer = correctAnswer;
    }
}
