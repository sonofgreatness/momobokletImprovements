package com.example.momobooklet_by_sm;

public class ActiveUser {
  private   boolean status ;
  private String agentPhone;
  ActiveUser(boolean status, String agentPhone){
      this.status =status;
      this.agentPhone=agentPhone;
  }
  public boolean getStatus(){
      return status;
  }

  public String getAgentPhone(){
      return agentPhone;
  }


  public void setAgentPhone(String agentPhone){
      this.agentPhone= agentPhone;


  }

    public void setStatus(boolean status){

      this.status= status;
    }
}
