import React, {useState} from 'react';
import {View, Image} from 'react-native';
import {WEInlineWidget} from 'react-native-we-personalization';
import PropTypes from 'prop-types';

const WEInlineWidgetTemplate = props => {
  const {
    androidPropertyId = '',
    iosPropertyId = 0,
    screenName = '',
    style = {},
    onRendered,
    onDataReceived,
    onPlaceholderException,
  } = props;
  const {height, width} = style;

  const [inlineStyle, setInlineStyle] = useState({height: 0, width: 0});
  const [showPlaceHolder, setShowPlaceHolder] = useState(true);

  const handleDataReceived = data => {
    onDataReceived(data);
  };

  const handlePlaceholderException = data => {
    onPlaceholderException(data);
  };

  const handleRendered = data => {
    setInlineStyle({height, width}); // update Height to make View visible
    setShowPlaceHolder(false); // Hide PlaceHolder Image
    onRendered(data);
  };

  return (
    <View>
      {/* Below view is hidden until the WEInlineWidget is rendered */}
      <View style={inlineStyle}>
        <WEInlineWidget
          screenName={screenName}
          androidPropertyId={androidPropertyId}
          iosPropertyId={iosPropertyId}
          style={style}
          onDataReceived={handleDataReceived}
          onPlaceholderException={handlePlaceholderException}
          onRendered={handleRendered}
        />
      </View>

      {/*  Comment below code to Hide the View */}
      {showPlaceHolder ? (
        <Image
          source={{uri: 'https://fakeimg.pl/640x360'}} // Replace with the actual PlaceHolder for the image
          style={style}
        />
      ) : null}
    </View>
  );
};

WEInlineWidgetTemplate.propTypes = {
  androidPropertyId: PropTypes.string,
  iosPropertyId: PropTypes.number,
  screenName: PropTypes.string,
  style: PropTypes.object,
  onDataReceived: PropTypes.func,
  onPlaceholderException: PropTypes.func,
  onRendered: PropTypes.func,
};

WEInlineWidgetTemplate.defaultProps = {
  androidPropertyId: '',
  iosPropertyId: 0,
  screenName: '',
  style: {},
  onDataReceived: () => {},
  onPlaceholderException: () => {},
  onRendered: () => {},
};

export default WEInlineWidgetTemplate;
