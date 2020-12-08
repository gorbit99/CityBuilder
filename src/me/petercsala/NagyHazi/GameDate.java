package me.petercsala.NagyHazi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the time in the game
 */
public class GameDate implements Serializable {
    /**
     * The year the game is in
     */
    int year;
    /**
     * The month the game is in
     */
    int month;
    /**
     * The day the game is in
     */
    int day;

    /**
     * The amount of time elapsed from the given day
     */
    float elapsedDay = 0;

    /**
     * The event functions to be called when the day changes
     */
    List<NewDayFunction> onDayChange = new ArrayList<>();

    /**
     * Constructor
     */
    public GameDate() {
        year = 2000;
        month = 5;
        day = 27;
    }

    /**
     * Advance the time
     *
     * @param elapsedTime The time since the last frame
     */
    void advance(float elapsedTime) {
        elapsedDay += elapsedTime;
        while (elapsedDay >= 3) {
            elapsedDay -= 5;
            day += 1;
            for (NewDayFunction newDayFunction : onDayChange) {
                newDayFunction.run();
            }
            if (day > 30) {
                day = 1;
                month++;
            }
            if (month > 12) {
                month = 1;
                year++;
            }
        }
    }

    /**
     * Get the current date as a string
     *
     * @return The resulting string
     */
    public String getDateString() {
        return String.format("%d.%02d.%02d", year, month, day);
    }

    /**
     * Register a day change event
     *
     * @param function The function to run
     */
    public void registerEvent(NewDayFunction function) {
        onDayChange.add(function);
    }

    /**
     * The interface representing a day change function
     */
    public interface NewDayFunction {
        /**
         * Run the function
         */
        void run();
    }

    /**
     * Read in object from a stream
     *
     * @param inputStream The stream to read from
     * @throws ClassNotFoundException Thrown, when the underlying interface throws a ClassNotFoundException
     * @throws IOException            Thrown, when the underlying interface throws an IOException
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        year = inputStream.readInt();
        month = inputStream.readInt();
        day = inputStream.readInt();
        onDayChange = new ArrayList<>();
    }

    /**
     * Write the object to a stream
     *
     * @param outputStream The stream to write to
     * @throws IOException Thrown, when the underlying interface throws an IOException
     */
    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(year);
        outputStream.writeInt(month);
        outputStream.writeInt(day);
    }
}
