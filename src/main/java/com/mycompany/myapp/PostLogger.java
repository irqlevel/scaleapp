package com.mycompany.myapp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.SPostLogger;

public class PostLogger implements SPostLogger {
    	private static final Logger log = LoggerFactory.getLogger(App.class);
    

        @Override
        public void debugMessage(String tag, String message) {
                log.debug("DEBUG:" + tag + ":" + message);
        }

        @Override
        public void errorMessage(String tag, String message) {
                log.error("ERROR:" + tag + ":" + message);
        }

        @Override
        public void exceptionMessage(String tag, Exception e) {
                Writer sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                log.error("EXCP:" + tag + ":" + e.toString() + " stack:" + sw.toString());
        }

        @Override
        public void infoMessage(String tag, String message) {
                log.info("INFO:" + tag + ":" + message);
        }

        @Override
        public void verboseMessage(String tag, String message) {
                log.info("VERB:" + tag + ":" + message);
        }

        @Override
        public String currentTime() {
                // TODO Auto-generated method stub

            DateTime dateTime = new DateTime(System.currentTimeMillis(),DateTimeZone.forTimeZone(TimeZone.getDefault()));
            DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS");
                
            return timeFormater.print(dateTime);
        }

        @Override
        public void throwableMessage(String tag, Throwable t) {
                // TODO Auto-generated method stub

                Writer sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                log.error("THRW:" + tag + ":" + t.toString() + " stack:" + sw.toString());
        }
}
