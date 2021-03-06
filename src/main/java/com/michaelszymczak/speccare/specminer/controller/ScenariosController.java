package com.michaelszymczak.speccare.specminer.controller;

import com.michaelszymczak.speccare.specminer.core.ScenarioResultJudge;
import com.michaelszymczak.speccare.specminer.core.ScenarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/scenarios")
class ScenariosController {
    @Autowired private ScenarioResultJudge judge;

    @RequestMapping(value="/{scenarioNameFragment}", method=RequestMethod.GET,  produces = { "application/json"})
    @ResponseBody
    public ResponseEntity<String> find(@PathVariable String scenarioNameFragment) throws IOException {
        ScenarioResponse scenarioResponse = judge.createResponse(scenarioNameFragment);
        return new ResponseEntity<>(scenarioResponse.getContent(), scenarioResponse.getHttpStatus());
    }
}