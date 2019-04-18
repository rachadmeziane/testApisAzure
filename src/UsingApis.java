public class UsingApis {

	public static string GetOAuthTokenFromAAD()
	{
		var authenticationContext = new AuthenticationContext(String.Format("{0}/{1}",
		ConfigurationManager.AppSettings["ADALServiceURL"], ConfigurationManager.AppSettings["TenantDomain"]));
		var result = authenticationContext.AcquireToken(
				String.Format("{0}/", ConfigurationManager.AppSettings["BillingServiceURL"]),
				ConfigurationManager.AppSettings["ClientID"],
				new Uri(ConfigurationManager.AppSettings["ADALRedirectURL"]));

		if (result == null) {
			throw new InvalidOperationException("Failed to obtain the JWT token");
		}
		return result.AccessToken;
	}

	public static  void GetUsageData()
	{
		usageload = null;
		MicrosoftResourcesDataType res;
		string requestURL = "";
		if (granularity == "Hourly")
		{
					requestURL = String.Format("{0}/{1}/{2}/{3}",
					ConfigurationManager.AppSettings["BillingServiceURL"],
					"subscriptions",
					ConfigurationManager.AppSettings["SubscriptionID"],
					// "providers/Microsoft.Commerce/UsageAggregates?api-version=2015-06-01-preview&reportedstartTime=" + StartTime.Date + "&reportedEndTime=" + EndTime.Date);
					"providers/Microsoft.Commerce/UsageAggregates?api-version=2015-06-01-preview&reportedStartTime=" + StartTime.Date + "&reportedEndTime=" + EndTime.Date + "&aggregationGranularity=Hourly&showDetails=true");
		}
		else
		{
			requestURL = String.Format("{0}/{1}/{2}/{3}",
					ConfigurationManager.AppSettings["BillingServiceURL"],
					"subscriptions",
					ConfigurationManager.AppSettings["SubscriptionID"],
					//"providers/Microsoft.Commerce/UsageAggregates?api-version=2015-06-01-preview&reportedstartTime="+StartTime.Date+"&reportedEndTime="+EndTime.Date);
					"providers/Microsoft.Commerce/UsageAggregates?api-version=2015-06-01-preview&reportedStartTime=" + StartTime.Date + "&reportedEndTime=" + EndTime.Date + "&aggregationGranularity=Daily&showDetails=true");
		}

		HttpWebRequest request = (HttpWebRequest)WebRequest.Create(requestURL);
		request.Headers.Add(HttpRequestHeader.Authorization, "Bearer " + token);
		request.Headers.Add(HttpRequestHeader.CacheControl, "no-cache");
		request.ContentType = "application/json";

		try
		{
			HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			Stream receiveStream = response.GetResponseStream();
			StreamReader readStream = new StreamReader(receiveStream, Encoding.UTF8);
			var usageResponse = readStream.ReadToEnd();
			usageload = JsonConvert.DeserializeObject<UsagePayload>(usageResponse);//(usageResponse,settings);
			var load = JsonConvert.SerializeObject(usageResponse);
			response.Close();
			readStream.Close();
		}

		catch (Exception ex)
		{
			throw;	
		}
	}

	public static void GetRateCardData()
	{
		string requestURL = String.Format("{0}/{1}/{2}/{3}",
				ConfigurationManager.AppSettings["BillingServiceURL"],
				"subscriptions",
				ConfigurationManager.AppSettings["SubscriptionID"],
				"providers/Microsoft.Commerce/RateCard?api-version=2015-06-01-preview&$filter=OfferDurableId eq 'MS-AZR-0121p' and Currency eq 'USD' and Locale eq 'en-US' and RegionInfo eq 'US'");
		HttpWebRequest request = (HttpWebRequest)WebRequest.Create(requestURL);
		request.Headers.Add(HttpRequestHeader.Authorization, "Bearer " + token);
		request.Headers.Add(HttpRequestHeader.CacheControl, "no-cache");
		request.ContentType = "application/json";
		
		try
		{
			HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			Stream receiveStream = response.GetResponseStream();
			StreamReader readStream = new StreamReader(receiveStream, Encoding.UTF8);
			var rateCardResponse = readStream.ReadToEnd();
			payload = JsonConvert.DeserializeObject<RateCardPayload>(rateCardResponse);
			response.Close();
			readStream.Close();
		}
		catch (Exception e)
		{
			throw ;
		}
	}
}
