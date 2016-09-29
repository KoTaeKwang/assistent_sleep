/*
 * accelerate.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

void _accerleration_value(sensor_h sensor, sensor_event_s *sensor_data, void
		*user_data)
{
	if( sensor_data->value_count < 3 )
		return;
	char buf[PATH_MAX];
	appdata_s *ad = (appdata_s*)user_data;
	sprintf(buf, "<align=center> <font_size=20> X : %0.1f / Y : %0.1f / Z : %0.1f </font> </align>",
			sensor_data->values[0], sensor_data->values[1], sensor_data->values[2]);
	elm_object_text_set(ad->accerlerateLabel, buf);
}

void
start_acceleration_sensor(appdata_s *ad)
{
	sensor_error_e err = SENSOR_ERROR_NONE;
	sensor_get_default_sensor(SENSOR_ACCELEROMETER, &accerlerate_info.sensor);
	err = sensor_create_listener(accerlerate_info.sensor, &accerlerate_info.sensor_listener);
	sensor_listener_set_option(accerlerate_info.sensor_listener,SENSOR_OPTION_ALWAYS_ON);
	sensor_listener_set_event_cb(accerlerate_info.sensor_listener, 100, _accerleration_value, ad);
	sensor_listener_start(accerlerate_info.sensor_listener);
}


