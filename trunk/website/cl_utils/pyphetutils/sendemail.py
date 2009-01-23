# Import smtplib for the actual sending function
import smtplib

def send_email(fromaddr, toaddrs, subject, rawmsg):
    msg = 'Subject: %s\n\n%s' % (subject, rawmsg)
    server = smtplib.SMTP('localhost')
    server.sendmail(fromaddr, toaddrs, msg)
    server.quit()
