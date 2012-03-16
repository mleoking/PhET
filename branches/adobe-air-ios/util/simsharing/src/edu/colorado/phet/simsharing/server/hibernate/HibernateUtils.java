//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server.hibernate;
//
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
///**
// * @author Sam Reid
// */
//public class HibernateUtils {
//
//    public static boolean wrapTransaction( Session session, HibernateTask task ) {
//        Transaction tx = null;
//        boolean ret;
//        try {
//            tx = session.beginTransaction();
//            tx.setTimeout( 600 );
//
//            ret = task.run( session );
//
//            //logger.debug( "tx isactive: " + tx.isActive() );
//            //logger.debug( "tx wascommited: " + tx.wasCommitted() );
//            if ( tx.isActive() ) {
//                tx.commit();
//            }
//            else {
//                //logger.warn( "tx not active", new RuntimeException( "exception made for stack trace" ) );
//            }
//        }
//        catch ( RuntimeException e ) {
//            ret = false;
//            if ( tx != null && tx.isActive() ) {
//                try {
//                    tx.rollback();
//                }
//                catch ( HibernateException e1 ) {
//                }
//                throw e;
//            }
//        }
//        return ret;
//    }
//
//    private static <T> Result<T> transactionCore( Session session, Task<T> task, boolean throwHibernateExceptions ) {
//        Transaction tx = null;
//        T ret = null;
//        try {
//            tx = session.beginTransaction();
//            tx.setTimeout( 600 );
//
//            ret = task.run( session );
//
//            if ( tx.isActive() ) {
//                tx.commit();
//            }
//
//            return new Result<T>( true, ret, null );
//        }
//        catch ( Exception e ) {
//            // TODO: check current levels of TaskExceptions
//            tryRollback( tx );
//            return new Result<T>( false, ret, e );
//        }
////        catch ( RuntimeException e ) {
////            tryRollback( tx );
////            if ( throwHibernateExceptions ) {
////                throw e;
////            }
////            else {
////                return new Result<T>( false, ret, e );
////            }
////        }
//    }
//
//    public static void tryRollback( Transaction tx ) {
//        if ( tx != null && tx.isActive() ) {
//            try {
//                tx.rollback();
//            }
//            catch ( HibernateException e1 ) {
//                throw e1;
//            }
//        }
//    }
//
//    public static <T> Result<T> resultTransaction( Session session, Task<T> task ) {
//        return transactionCore( session, task, true );
//    }
//
//    public static boolean resultTransaction( Session session, final VoidTask task ) {
//        return resultTransaction( session, voidToVoid( task ) ).success;
//    }
//
//    private static Task<Void> voidToVoid( final VoidTask task ) {
//        return new Task<Void>() {
//            public Void run( Session session ) {
//                task.run( session );
//                return null;
//            }
//        };
//    }
//}
