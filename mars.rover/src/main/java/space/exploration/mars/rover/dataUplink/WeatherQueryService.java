package space.exploration.mars.rover.dataUplink;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.exploration.mars.rover.utils.WeatherUtil;

public class WeatherQueryService extends QueryService {
    private Logger logger = LoggerFactory.getLogger(WeatherQueryService.class);

    @Override
    public String getQueryString() {
        return "http://marsweather.ingenology.com/v1/latest/";
    }

    @Override
    public Object getResponse() {
        Document                   document       = Jsoup.parse(getResponseAsString());
        WeatherData.WeatherPayload weatherPayload = WeatherUtil.getweatherPayload(document.body().text());

        return weatherPayload;
    }
}
