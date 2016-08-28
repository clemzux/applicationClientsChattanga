package com.example.clemzux.applicationclientschattanga.utilitaries;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemzux on 25/08/16.
 */
public class CJsonDecoder<T> {

    public T Decoder(String pJson, Class pType) throws IOException
    {
        return (T) new ObjectMapper().readValue(pJson, pType);
    }


    public List<T> DecoderList (String pJson, Class pType) throws IOException, JSONException
    {
        return new ObjectMapper ().readValue(pJson, new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, pType));
    }
}
