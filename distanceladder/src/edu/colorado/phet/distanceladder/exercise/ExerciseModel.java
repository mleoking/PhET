/**
 * Class: ExerciseModel
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:24:53 PM
 */
package edu.colorado.phet.distanceladder.exercise;

public abstract class ExerciseModel {
    private String question;
    private Answer[] choices;
    private Answer correctAnswer;

    public ExerciseModel() {
    }

    public ExerciseModel( String question, Answer[] choices, Answer correctAnswer ) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    abstract public boolean evaluate( Answer choice );

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
