package com.webengage.personalization.data

import android.util.JsonReader
import android.util.JsonToken
import com.webengage.personalization.utils.JsonKeys

class WELayoutDataTypeAdapter {
    var parserType = ""
    fun read(reader: JsonReader, targetViewId: String): WECampaignData {
        reader.beginObject()
        var fieldName = ""

        var content = WECampaignContent()
        //var targetViewId = ""

        while (reader.hasNext()) {
            val token = reader.peek()
            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldName = reader.nextName()
            }
            when {
                JsonKeys.TEMPLATE_DATA.equals(fieldName, true) -> {
                    content = readTemplateData(reader)
                }
                else -> {
                    reader.skipValue();
                }
            }
        }
        return WECampaignData(parserType, targetViewId, content)
    }

    private fun readTemplateData(reader: JsonReader): WECampaignContent {
        var layoutContent = WECampaignContent()
        reader.isLenient = true
        reader.beginObject()
        var fieldName = ""
        while (reader.hasNext()) {
            val token = reader.peek()
            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldName = reader.nextName()
            }
            when {
                JsonKeys.CONFIG.equals(fieldName, ignoreCase = true) -> {
                    layoutContent = readConfig(reader)
                }
                JsonKeys.PARSER_TYPE.equals(fieldName, ignoreCase = true) -> {
                    parserType = reader.nextString()
                }
                else -> {
                    reader.skipValue();
                }

            }
        }
        reader.endObject()
        return layoutContent
    }

    private fun readConfig(reader: JsonReader): WECampaignContent {
        var layoutContent = WECampaignContent()
        reader.isLenient = true
        reader.beginObject()
        var fieldName = ""
        while (reader.hasNext()) {

            val token = reader.peek()
            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldName = reader.nextName()
            }
            when {
                JsonKeys.CONTENT.equals(fieldName, ignoreCase = true) -> {
                    layoutContent = readContent(reader)
                }
                else -> {
                    reader.skipValue();
                }

            }
        }
        reader.endObject()
        return layoutContent
    }


    private fun readContent(reader: JsonReader): WECampaignContent {

        val layoutContent = WECampaignContent()
        reader.isLenient = true
        reader.beginObject()
        var fieldName = ""
        while (reader.hasNext()) {
            val token = reader.peek()
            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldName = reader.nextName()
            }
            when {
                JsonKeys.SUB_LAYOUT_TYPE.equals(fieldName, ignoreCase = true) -> {
                    //move to next token
                    reader.peek()
                    layoutContent.subLayoutType = reader.nextString()
                }
                JsonKeys.LAYOUT_TYPE.equals(fieldName, ignoreCase = true) -> {
                    //move to next token
                    reader.peek()
                    layoutContent.layoutType = reader.nextString()
                }
                JsonKeys.CHILDREN.equals(fieldName, ignoreCase = true) -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        layoutContent.children.add(readContent(reader))
                    }
                    reader.endArray()
                }
                JsonKeys.CUSTOM.equals(fieldName, ignoreCase = true) -> {
                    //reader.skipValue()
                    reader.beginArray()
                    while (reader.hasNext()) {
                        layoutContent.customData = readCustomValues(reader)
                    }
                    reader.endArray()
                }
                else -> {
                    val propertyValue = when (reader.peek()) {
                        JsonToken.STRING -> {
                            reader.nextString()
                        }
                        JsonToken.BOOLEAN -> {
                            reader.nextBoolean()
                        }
                        JsonToken.NUMBER -> {
                            reader.nextDouble()
                        }
                        else -> {
                            reader.skipValue()
                        }
                        /*JsonToken.BEGIN_ARRAY -> TODO()
                        JsonToken.END_ARRAY -> TODO()
                        JsonToken.BEGIN_OBJECT -> TODO()
                        JsonToken.END_OBJECT -> TODO()
                        JsonToken.NAME -> TODO()
                        JsonToken.NULL -> TODO()
                        JsonToken.END_DOCUMENT -> TODO()
                        null -> TODO()*/
                    }
                    layoutContent.properties[fieldName] = propertyValue!!
                }
            }
        }
        reader.endObject()
        return layoutContent
    }

    private fun readCustomValues(reader: JsonReader): HashMap<String, Any> {
        val customData: HashMap<String, Any> = hashMapOf()
        reader.isLenient = true
        var customKey = ""
        var customValue = ""
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                val token = reader.peek()
                var fieldName: String = ""
                if (token.equals(JsonToken.NAME)) {

                    fieldName = reader.nextName()
                }
                when {
                    JsonKeys.KEY.equals(fieldName, ignoreCase = true) -> {
                        customKey = reader.nextString()
                    }
                    JsonKeys.VALUE.equals(fieldName, ignoreCase = true) -> {
                        customValue = reader.nextString()
                    }
                    else -> {
                        reader.skipValue()
                    }
                }
            }
            if (!customKey.isNullOrEmpty() && !customValue.isNullOrEmpty() && customValue.isNotBlank()) {
                customData[customKey] = customValue
                customKey = ""
                customValue = ""
            }
            reader.endObject();
        }
        return customData
    }
}