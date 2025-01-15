import WEInlineWidget from './view/WEInlineWidget';
import {
  registerWECampaignCallback,
  deregisterWECampaignCallback,
} from './callbacks/WECampaignData';
import {
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  trackClick,
  trackImpression,
} from './callbacks/WEPlaceHolder';

import { initWePersonalization } from './bridge/WEPersonalizationBridge';

export {
  WEInlineWidget,
  initWePersonalization,
  registerWECampaignCallback,
  deregisterWECampaignCallback,
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  trackClick,
  trackImpression,
};

