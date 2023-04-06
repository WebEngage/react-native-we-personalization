import WEInlineWidget from './view/WEInlineWidget';
import {
  registerWECampaignCallback,
  deregisterWECampaignCallback,
  userWillHandleDeepLink, // check with milind - TODO

} from './callbacks/WECampaignData';
import {
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  trackClick,
  trackImpression,
} from './callbacks/WEPlaceHolder';

export {
  WEInlineWidget,
  registerWECampaignCallback,
  deregisterWECampaignCallback,
  registerWEPlaceholderCallback,
  deregisterWEPlaceholderCallback,
  userWillHandleDeepLink,
  trackClick,
  trackImpression,
};
