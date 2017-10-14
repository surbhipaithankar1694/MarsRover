package space.exploration.mars.rover.dataUplink;

public interface isSpaceQuery {

    void setAuthenticationKey(String authenticationKey);

    void setEarthStartDate(long startMs);

    void setEarthEndDate(long endMs);

    Object getQueryResponseType();

    String getQueryString();

    void executeQuery();

    Object getResponse();

    String getResponseAsString();
}
