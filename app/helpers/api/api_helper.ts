'use strict'
import { NativeModules } from 'react-native'

export type AccessToken = {
  accessToken: string
}

interface ApiHelper {
  // Native process returns an API access token
  // in a property named 'accessToken' in the
  // promise passed in
  getAccessToken(): Promise<{ accessToken: string }>

  //   Native process determines if access token
  //   should be refreshed based on response code
  //   and returns a boolean property named 'shouldRefresh'
  //   in the promise passed in
  shouldRefreshAccessToken(
    responseCode: number
  ): Promise<{ shouldRefresh: boolean }>

  //   Native processes refreshes the access token
  //   and returns the new access token in a
  //   property named 'accessToken' in the promise
  //   passed in
  refreshAccessToken(): Promise<{ accessToken: string }>

  //   Native process gets the host
  //   (i.e. 'api.planningcenteronline.com' for production)
  //   to use while making API calls and returns the
  //   value in a property named 'host' in the promise
  //   passed in
  getApiHost(): Promise<{ host: string }>

  // Native process gets the user agent to
  // use while making API calls and returns the
  // value in a property named 'userAgent' in the
  // promise passed in
  getUserAgent(): Promise<{ userAgent: string }>
}

export default NativeModules.ApiHelper as ApiHelper
