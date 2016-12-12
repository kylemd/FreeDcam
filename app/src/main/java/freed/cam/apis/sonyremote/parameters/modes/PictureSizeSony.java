/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.sonyremote.parameters.modes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.FreeDPool;

/**
 * Created by troop on 15.12.2014.
 */
public class PictureSizeSony extends BaseModeParameterSony
{
    final String TAG = PictureSizeSony.class.getSimpleName();
    public PictureSizeSony(SimpleRemoteApi api)
    {
        super("getStillSize", "setStillSize", "getAvailableStillSize", api);
    }

    @Override
    public String[] GetValues()
    {
        jsonObject =null;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                try {
                    jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                    values = processValuesToReturn();
                    onValuesHasChanged(values);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });



        return values;
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        try
        {
            String[] split = valueToSet.split("x");
            try {
                JSONArray array = new JSONArray().put(0, split[0]).put(1, split[1]);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = new String[subarray.length()];
            for (int i =0; i < subarray.length(); i++)
            {
                JSONObject size = subarray.getJSONObject(i);
                ret[i] = size.getString("aspect") + "x" +size.getString("size");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    protected String processGetString() {
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("result");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        String ret ="";
        try
        {
            JSONObject size = array.getJSONObject(0);
            ret = size.getString("aspect") + "+" +size.getString("size");
        } catch (JSONException | NullPointerException ex)
        {
            ex.printStackTrace();
        }
        return ret;
    }
}
