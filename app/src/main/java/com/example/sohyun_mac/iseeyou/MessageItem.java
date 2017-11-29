package com.example.sohyun_mac.iseeyou;

/**
 * Created by sohyun_mac on 2017. 11. 29..
 */

public class MessageItem {
    String messageId;
    String threadId;
    String phoneNum;
    String contactId;
    String contactId_string;
    String time;
    String body;

    public MessageItem(){}

    public String getMessageId(){
        return messageId;
    }

    public void setMessageId(String messageId){
        this.messageId = messageId;
    }

    public String getThreadId(){
        return threadId;
    }

    public void setThreadId(String threadId){
        this.threadId = threadId;
    }

    public String getPhoneNum(){
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId_string() {
        return contactId_string;
    }

    public void setContactId_string(String contactId_string) {
        this.contactId_string = contactId_string;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
