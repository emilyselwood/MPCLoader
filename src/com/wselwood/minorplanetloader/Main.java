package com.wselwood.minorplanetloader;

import com.wselwood.mpcreader.InvalidDataException;
import com.wselwood.mpcreader.MinorPlanet;
import com.wselwood.mpcreader.MinorPlanetReader;
import com.wselwood.mpcreader.MinorPlanetReaderBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.Properties;

/**
 *
 */
public class Main {

    public static void main(String[] args) {

        Connection connection = null;
        MinorPlanetReader reader = null;
        PreparedStatement insert = null;
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(Paths.get("./connection.properties"), StandardOpenOption.READ));
        }
        catch(IOException e) {
            System.out.println("could not load connection.properties");
            e.printStackTrace();
            return;
        }

        if (args.length < 1) {
            System.out.println("Please pass a list of file names as arguments");
            return;
        }

        try {
            Class.forName(props.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {

            connection = DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
            insert = createInsert(connection);

            // loop over all the arguments and load each one.
            for (String arg : args) {

                // create the reader. Given the file to be loaded.
                reader = new MinorPlanetReaderBuilder()
                        .open(new File(arg))
                        .build();

                long start = System.currentTimeMillis(); // record how long this takes.

                int lineNumber = 1;
                int batchCount = 0;
                while (reader.hasNext()) {   // check to see if we have more data in the file. If so loop.
                    MinorPlanet mp = reader.next(); // get the next record.

                    addMPToBatch(insert, mp);

                    lineNumber = lineNumber + 1;

                    batchCount = batchCount + 1;
                    if (batchCount >= 1000) {
                        insert.executeBatch();
                        batchCount = 0;
                    }
                }
                insert.executeBatch(); // insert remaining records

                // now work out how long this actually took
                long end = System.currentTimeMillis();
                System.out.println("processed " + lineNumber + " records in " + (end - start) + " milliseconds");

                reader.close();
            }

        } catch (BatchUpdateException e) {
            e.printStackTrace();
            e.getNextException().printStackTrace();
        }catch (IOException | InvalidDataException | SQLException e) {
            e.printStackTrace();
        } finally {
            if(insert != null) {
                try {
                    insert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(reader != null) {
                try {
                    reader.close(); // close down the file reader now we are done with it.
                } catch (IOException e) {
                    e.printStackTrace(); // nothing else we can do with this.
                }
            }
        }

    }

    private static PreparedStatement createInsert(Connection connection) throws SQLException {
        String sql = "insert into minorplanet (identifier,absolutemagnitude,slope,epoch,meananomalyepoch," +
                "argumentofperihelion,longitudeoftheascendingnode,inclinationtotheecliptic,orbitaleccentricity," +
                "meandailymotion,semimajoraxis,uncertaintyparameter,reference,numberofobservations,numberofoppositions," +
                "rmsresidual,coarseindicatorofperturbers,preciseindicatorofperturbers,computername,hexdigitflags," +
                "readabledesignation,dateoflastobservation,yearoffirstobservation,yearoflastobservation,arclength)" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement;
    }

    private static void addMPToBatch(PreparedStatement insert, MinorPlanet mp) throws SQLException {
        insert.setString(1, mp.getNumber());
        if(mp.getAbsoluteMagnitude() != null) {
            insert.setDouble(2, mp.getAbsoluteMagnitude());
        }
        else {
            insert.setNull(2, Types.DOUBLE);
        }
        if(mp.getSlope() != null) {
            insert.setDouble(3, mp.getSlope());
        }
        else {
            insert.setNull(3, Types.DOUBLE);
        }
        insert.setDate  (4, new Date(mp.getEpoch().getTime()));
        insert.setDouble(5, mp.getMeanAnomalyEpoch());
        insert.setDouble(6, mp.getArgumentOfPerihelion());
        insert.setDouble(7, mp.getLongitudeOfTheAscendingNode());
        insert.setDouble(8, mp.getInclinationToTheEcliptic());
        insert.setDouble(9, mp.getOrbitalEccentricity());
        insert.setDouble(10, mp.getMeanDailyMotion());
        insert.setDouble(11, mp.getSemimajorAxis());
        insert.setString(12, mp.getUncertaintyParameter());
        insert.setString(13, mp.getReference());

        if(mp.getNumberOfObservations() != null) {
            insert.setInt   (14, mp.getNumberOfObservations());
        }
        else {
            insert.setNull(14, Types.INTEGER);
        }

        if(mp.getNumberOfOppositions() != null) {
            insert.setInt   (15, mp.getNumberOfOppositions());
        }
        else {
            insert.setNull(15, Types.INTEGER);
        }

        if(mp.getrMSResidual() != null) {
            insert.setDouble(16, mp.getrMSResidual());
        }
        else {
            insert.setNull(16, Types.DOUBLE);
        }

        insert.setString(17, mp.getCoarseIndicatorOfPerturbers());
        insert.setString(18, mp.getPreciseIndicatorOfPerturbers());
        insert.setString(19, mp.getComputerName());
        insert.setInt   (20, mp.getHexDigitFlags());
        insert.setString(21, mp.getReadableDesignation());
        insert.setDate  (22, new Date(mp.getDateOfLastObservation().getTime()));
        insert.setInt   (23, mp.getYearOfFirstObservation());
        insert.setInt   (24, mp.getYearOfLastObservation());
        insert.setInt   (25, mp.getArcLength());

        insert.addBatch();
    }

}
