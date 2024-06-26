package com.plenigo.nasashell;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "nasaFeignClient", url = "https://api.nasa.gov/EPIC")
public interface NasaFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/natural/date/{date}", produces = "application/json")
    List<NasaImageDto> getImagesByDate(@PathVariable(value = "date", required = false) String date, @RequestParam("api_key") String apiKey);

    @RequestMapping(method = RequestMethod.GET, value = "/api/natural/all", produces = "application/json")
    List<NasaDateDto> getAvailableDates(@RequestParam("api_key") String apiKey);

    @RequestMapping(method = RequestMethod.GET, value = "/archive/natural/{year}/{month}/{day}/png/{imageName}", produces = "image/png")
    byte[] getImage(@PathVariable(value = "year") String year,
                               @PathVariable(value = "month") String month,
                               @PathVariable(value = "day") String day,
                               @PathVariable(value = "imageName") String imageName,
                               @RequestParam("api_key") String apiKey);
}
